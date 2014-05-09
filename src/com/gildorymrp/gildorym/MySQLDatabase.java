package com.gildorymrp.gildorym;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import com.gildorymrp.gildorymclasses.CharacterClass;
import com.gildorymrp.charactercards.CharacterBehavior;
import com.gildorymrp.charactercards.CharacterCard;
import com.gildorymrp.charactercards.CharacterMorality;
import com.gildorymrp.charactercards.Gender;
import com.gildorymrp.charactercards.Race;
import com.gildorymrp.charactercards.Subrace;
import com.gildorymrp.gildorymclasses.CharacterProfession;

public class MySQLDatabase {

	private static final String REPLACE_CHAR_STATEMENT =
			"REPLACE INTO characters (" +
					"uid, " +
					"char_name, " +
					"minecraft_account_name, " +
					"age, " +
					"gender, " +
					"description, " +
					"race, " +
					"sub_race, " +
					"health, " +
					"`class`, " +
					"profession1, " +
					"profession2, " +
					"specialization_id, " +
					"deity, " + 
					"birthday, " +
					"`level`, " +
					"experience, " +
					"stats_uid," +
					"morality, " +
					"behavior, " +
					"wounds_id, " +
					"x, " +
					"y, " +
					"z, " +
					"world) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

	private static final String INSERT_CHAR_STATEMENT =
			"INSERT INTO characters (" +
					"char_name, " +
					"minecraft_account_name, " +
					"age, " +
					"gender, " +
					"description, " +
					"race, " +
					"sub_race, " +
					"health, " +
					"`class`, " +
					"profession1, " +
					"profession2, " +
					"specialization_id, " +
					"deity, " + 
					"birthday, " +
					"`level`, " +
					"experience, " +
					"stats_uid, " +
					"morality, " +
					"behavior, " +
					"wounds_id, " +
					"x, " +
					"y, " +
					"z, " +
					"world) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

	private static final String SELECT_CHAR_STATEMENT =
			"SELECT * FROM characters WHERE uid=?";

	private static final String REPLACE_PLAYER_CUR_CHAR =
			"UPDATE  players SET current_character_uid = ? WHERE minecraft_account_name = ?;";
	private static final String REPLACE_PLAYER_CUR_CHAR_CREATED_CHAR =
			"REPLACE INTO players (" +
					"minecraft_account_name, " +
					"created_characters_id, " +
					"current_character_uid) VALUES (?, ?, ?);";

	private static final String SELECT_CUR_CHAR_CREATED =
			"SELECT * FROM players WHERE minecraft_account_name=?";

	private static final String SELECT_CREATED_CHARS =
			"SELECT * FROM created_characters WHERE id = ?;";

	private static final String SELECT_CREATED_CHAR_BY_UID = 
			"SELECT * FROM created_characters WHERE char_uid = ?";

	private static final String INSERT_OR_UPDATE_CREATED_CHAR =
			"REPLACE INTO created_characters (id, char_uid, created_utc, generation_method, stored_utc) " +
					"VALUES(?, ?, ?, ?, ?);";

	private static final String CLEAR_CREATED_CHARS =
			"DELETE FROM created_characters WHERE id = ?;";
	
	private static final String INSERT_WOUND = 
			"INSERT INTO wounds (wound_id, timestamp, damage_type, " +
			"damage_amount, regen_time, potion_effect, effect_level, notes) " +
			"VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
	
	private static final String SELECT_WOUNDS_BY_ID =
			"SELECT * FROM wounds WHERE wound_id = ?";
	
	private static final String PRUNE_WOUNDS_BY_ID = 
			"DELETE FROM wounds WHERE wound_id = ? AND regen_time < ?";
	
	private static final String DELETE_WOUND =
			"DELETE FROM wounds WHERE wound_uid = ? LIMIT 1";
	
	private static final String INSERT_STATS = 
			"INSERT INTO stats (stamina, magical_stamina, lockpick_stamina) VALUES(?, ?, ?)";
	
	private static final String REPLACE_STATS = 
			"INSERT INTO stats (stats_uid, stamina, magical_stamina, lockpick_stamina) VALUES(?, ?, ?, ?)";
	
	private static final String SELECT_STATS_BY_ID = 
			"SELECT * FROM stats WHERE stats_uid = ?";
	
	private static final String DELETE_STATS =
			"DELETE FROM stats WHERE stats_uid = ? LIMIT 1";
	
	private static final String INSERT_SPECIALIZATION = 
			"INSERT INTO specializations (child_specialization, " +
			"base_attack, fort_save, ref_save, will_save, feat_id) VALUES(?, ?, ?, ?, ?, ?)";
	
	private static final String REPLACE_SPECIALIZATION = 
			"INSERT INTO specializations (specialization_uid, child_specialization, " +
			"base_attack, fort_save, ref_save, will_save, feat_id) VALUES(?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SELECT_SPECIALIZATION_BY_ID = 
			"SELECT * FROM specializations WHERE specialization_uid = ?";
	
	@SuppressWarnings("unused")
	private static final String DELETE_SPECIALIZATION = // TODO implement this
			"DELETE FROM specializations WHERE specialization_uid = ? LIMIT 1";
	
	private final String HOSTNAME;
	private final String PORT;
	private final String DATABASE;
	private final String USERNAME;
	private final String PASSWORD;
	private JavaPlugin plugin;
	private Connection conn;

	public MySQLDatabase(JavaPlugin plugin, String hostname, String port, String database, String username, String password) {
		HOSTNAME = hostname;
		PORT = port;
		DATABASE = database;
		USERNAME = username;
		PASSWORD = password;
		this.plugin = plugin;
		conn = null;
	}

	public boolean connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" + DATABASE, USERNAME, PASSWORD);
			return true;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to establish MySQL connection to " + HOSTNAME + ":" + PORT + "/" + DATABASE);
			ex.printStackTrace();
		} catch (Exception ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to detect JDBC drivers for MySQL!");
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Initializes the database as necessary.
	 */
	public void initDatabase() {
		if(!isConnected()) {
			throw new NullPointerException("Not connected to the database");
		}
		try {
			Statement statement = conn.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS `characters` (" +
					"`uid` int(11) NOT NULL AUTO_INCREMENT," +
					"`char_name` text," +
					"`minecraft_account_name` text," +
					"`age` int(11) DEFAULT NULL," +
					"`gender` varchar(10) DEFAULT NULL," +
					"`description` text," +
					"`race` varchar(20) DEFAULT NULL," +
					"`sub_race` TEXT DEFAULT NULL," +
					"`health` int(11) DEFAULT NULL," +
					"`class` text," +
					"`profession1` varchar(20)," +
					"`profession2` varchar(20)," +
					"`specialization_id` int," +
					"`deity` TEXT DEFAULT NULL, " + 
					"`birthday` int(11) DEFAULT NULL, " +
					"`level` int(11) DEFAULT NULL," +
					"`experience` int(11) DEFAULT NULL," +
					"`morality` varchar(10) DEFAULT NULL," +
					"`behavior` varchar(10) DEFAULT NULL," +
					"`stats_uid` int(11) DEFAULT NULL," +
					"`wounds_id` int(11) DEFAULT NULL," +
					"`x` double DEFAULT NULL," +
					"`y` double DEFAULT NULL," +
					"`z` double DEFAULT NULL," +
					"`world` varchar(20) DEFAULT 'world', " +
					"PRIMARY KEY (`uid`)" +
					");");

			statement.execute("CREATE TABLE IF NOT EXISTS `players` (" +
					"`minecraft_account_name` varchar(100) NOT NULL," +
					"`created_characters_id` int(11) DEFAULT NULL," +
					"`current_character_uid` int(11) DEFAULT NULL," +
					"PRIMARY KEY (`minecraft_account_name`));");

			statement.execute("CREATE TABLE IF NOT EXISTS `created_characters` (" +
					"`id` int(11) NOT NULL, " +
					"`char_uid` int(11) NOT NULL DEFAULT -1," +
					"`created_utc` BIGINT NOT NULL DEFAULT -1, " +
					"`generation_method` TEXT DEFAULT NULL, " +
					"`stored_utc` BIGINT NOT NULL DEFAULT -1, " +
					"PRIMARY KEY (`char_uid`));");
			
			statement.execute("CREATE TABLE IF NOT EXISTS `wounds` (" +
					"`wound_uid` int(11) NOT NULL AUTO_INCREMENT, " +
					"`wound_id` int(11) NOT NULL, " +
					"`timestamp` BIGINT NOT NULL DEFAULT -1," +
					"`damage_type` text DEFAULT NULL, " +
					"`damage_amount` TINYINT DEFAULT NULL," +
					"`regen_time` BIGINT NOT NULL DEFAULT -1," +
					"`potion_effect` int(11) NOT NULL, " +
					"`effect_level` int(11) NOT NULL, " + 
					"`notes` text DEFAULT NULL, " +
					"PRIMARY KEY (`wound_uid`)" +
					");");
			statement.execute("CREATE TABLE IF NOT EXISTS `stats` (" +
					"`stats_uid` int(11) NOT NULL AUTO_INCREMENT, " +
					"`stamina` int(11) DEFAULT NULL, " +
					"`magical_stamina` int(11) DEFAULT NULL," +
					"`lockpick_stamina` int(11) DEFAULT NULL," +
					"PRIMARY KEY (`stats_uid`)" +
					");");
			statement.execute("CREATE TABLE IF NOT EXISTS `specializations` (" +
					"`specialization_uid` int(11) NOT NULL AUTO_INCREMENT, " +
					"`child_specialization` int(11) NOT NULL DEFAULT -1, " +
					"`base_attack` int(11) DEFAULT NULL, " +
					"`fort_save` int(11) DEFAULT NULL, " +
					"`ref_save` int(11) DEFAULT NULL, " +
					"`will_save` int(11) DEFAULT NULL, " +
					"`feat_id` int(11) DEFAULT NULL, " +
					"PRIMARY KEY (`specialization_uid`)" +
					");");
		}catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Use reflection if you want to do this, this is for testing
	 * only
	 */
	@SuppressWarnings("unused")
	private void deleteEverything() {
		try {
			Statement statement = conn.createStatement();
			statement.execute("DROP TABLE IF EXISTS characters, players, created_characters, wounds, stats");
		}catch(SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean isConnected() {
		return (conn != null);
	}

	public void disconnect() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, "Unable to disconnect MySQL connection to " + HOSTNAME + ":" + PORT + "/" + DATABASE);
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Saves the specified gildorym character into the 'characters' table,
	 * doing an insert if the characters uid is -1, otherwise doing a replace
	 * 
	 * In the event that an insert is made {@code gChar} will be modified to
	 * resemble the new character id, and the created character id will be created
	 * and accessible by {@code getActive(gChar.getMcName()}
	 * 
	 * This does not save the actual wounds and the actual stats, if those need to be
	 * saved another function call is required.
	 * 
	 * @param gChar the character to save / update
	 * @return success if no error has occured
	 */
	public boolean saveCharacter(GildorymCharacter gChar) {
		try {
			int position = 1; // In the event that we are including ID, then we must increase these
			// numbers by this amount.
			PreparedStatement statement = null;
			if(gChar.getUid() != -1) {
				statement = conn.prepareStatement(REPLACE_CHAR_STATEMENT);

				statement.setInt(1, gChar.getUid());
				position = 2;

			}else {
				statement = conn.prepareStatement(INSERT_CHAR_STATEMENT);
			}
			
			if(gChar.getStats().getStatsUid() == -1) {
				saveStats(gChar.getStats());
			}

			statement.setString(position++, gChar.getName());
			statement.setString(position++, gChar.getMcName());
			statement.setInt(position++, gChar.getCharCard().getAge());
			statement.setString(position++, gChar.getCharCard().getGender().name());
			statement.setString(position++, gChar.getCharCard().getDescription());
			statement.setString(position++, gChar.getCharCard().getRace().name());
			Subrace subrace = 
					gChar.getCharCard().getSubrace() == null ? Subrace.defaultSubrace(gChar.getCharCard().getRace()) :
					gChar.getCharCard().getSubrace();
			statement.setString(position++, subrace.name());
			statement.setInt(position++, gChar.getCharCard().getHealth());
			statement.setString(position++, gChar.getCharClass() != null ? gChar.getCharClass().name() : null);
			statement.setString(position++, gChar.getProfession1() == null ? null : gChar.getProfession1().name());
			statement.setString(position++, gChar.getProfession2() == null ? null : gChar.getProfession2().name());
			statement.setInt(position++, gChar.getSpecialization() == null ? -1 : gChar.getSpecialization().getId());
			statement.setString(position++, gChar.getDeity());
			statement.setInt(position++, gChar.getBirthday());
			statement.setInt(position++, gChar.getLevel());
			statement.setInt(position++, gChar.getExperience());
			statement.setInt(position++, gChar.getStats().getStatsUid());
			statement.setString(position++, gChar.getCharCard().getMorality() != null ? gChar.getCharCard().getMorality().name() : null);
			statement.setString(position++, gChar.getCharCard().getBehavior() != null ? gChar.getCharCard().getBehavior().name() : null);
			statement.setInt(position++, gChar.getWoundsID());
			statement.setDouble(position++, gChar.getX());
			statement.setDouble(position++, gChar.getY());
			statement.setDouble(position++, gChar.getZ());
			statement.setString(position++, gChar.getWorld());

			statement.execute();

			if(gChar.getUid() == -1) {
				statement = conn.prepareStatement("SELECT LAST_INSERT_ID()");
				ResultSet results = statement.executeQuery();

				results.next();
				int uid = results.getInt(1);
				results.close();
				gChar.setUid(uid);
				
				int[] activeAndCreated = getActive(gChar.getMcName());
				int id = 0;
				if(activeAndCreated != null)
					id = activeAndCreated[1];
				else {
					statement = conn.prepareStatement("SELECT max(created_characters_id) FROM players");
					results = statement.executeQuery();

					results.next();
					id = results.getInt(1) + 1;
				}
				
				this.setPlayerCharactersCreatedAndActive(gChar.getMcName(), id, gChar.getUid());
				results.close();
			}
			return true;

		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to save character " + gChar + "!");
			e.printStackTrace();
			System.exit(0);
		}
		return false;
	}

	/**
	 * Loads the character into memory. It should be noted that
	 * for the wounds and stats, only the id is loaded and not the
	 * underlying data. If those need to be loaded (which they probably do),
	 * the appropriate funciton must be called
	 * 
	 * @param uid
	 * @return
	 */
	public GildorymCharacter loadCharacter(int uid) {
		PreparedStatement statement;
		try {
			statement = conn.prepareStatement(SELECT_CHAR_STATEMENT);
			statement.setInt(1, uid);

			ResultSet results = statement.executeQuery();

			results.next();

			GildorymCharacter result = new GildorymCharacter(uid);
			result.setName(results.getString("char_name"));
			result.setMcName(results.getString("minecraft_account_name"));
			result.setCharCard(new CharacterCard(
					results.getInt("age"),
					Gender.valueOf(results.getString("gender")),
					results.getString("description"),
					Race.valueOf(results.getString("race")),
					Subrace.valueOf(results.getString("sub_race")),
					results.getInt("health"),
					CharacterClass.valueOf(results.getString("class")), 
					results.getString("behavior") != null ? 
							CharacterBehavior.valueOf(results.getString("behavior")) : CharacterBehavior.NEUTRAL, 
							results.getString("morality") != null ? CharacterMorality.valueOf(results.getString("morality")) : CharacterMorality.NEUTRAL));
			result.setCharClass(CharacterClass.valueOf(results.getString("class")));
			result.setProfession1(results.getString("profession1") == null ? null : CharacterProfession.valueOf(results.getString("profession1")));
			result.setProfession2(results.getString("profession2") == null ? null : CharacterProfession.valueOf(results.getString("profession2")));
			result.setSpecialization(getSpecialization(results.getInt("specialization_id")));
			result.setDeity(results.getString("deity"));
			result.setBirthday(results.getInt("birthday"));
			result.setLevel(results.getInt("level"));
			result.setExperience(results.getInt("experience"));
			result.setStats(new GildorymStats(results.getInt("stats_uid")));
			result.setWoundsID(results.getInt("wounds_id"));
			result.setX(results.getDouble("x"));
			result.setY(results.getDouble("y"));
			result.setZ(results.getDouble("z"));
			result.setWorld(results.getString("world"));

			results.close();


			return result;
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to load character with uid " + uid + "!");
			e.printStackTrace();
		}

		return null;
	}

	public boolean setCurrentCharacter(String playerName, int uid) {
		try {
			PreparedStatement statement = conn.prepareStatement(REPLACE_PLAYER_CUR_CHAR);
			statement.setInt(1, uid);
			statement.setString(2, playerName);
			statement.execute();
			return true;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to set active character of " + playerName + " to " + uid + "!");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * createdId ought to match with the players first created characters uid, although this
	 * is not required
	 *
	 * @param playerName the players minecraft account name
	 * @param createdId the id associated with characters created by this player
	 * @param uid the active characters unique identifier
	 */
	public boolean setPlayerCharactersCreatedAndActive(String playerName, int createdId, int uid) {
		try {
			PreparedStatement statement = conn.prepareStatement(REPLACE_PLAYER_CUR_CHAR_CREATED_CHAR);
			statement.setString(1, playerName);
			statement.setInt(2, createdId);
			statement.setInt(3, uid);
			statement.execute();
			return true;
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to set active character of " + playerName + " to " + uid + "!");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Returns the active character uid and the list of created
	 * character ids, such that the first (0) position in the array
	 * is the active and the second (1) position in the array is
	 * the id to find all of the created characters by this player
	 *
	 * @param playerName
	 * @return active character uid, created characters id or null
	 */
	public int[] getActive(String playerName) {
		PreparedStatement statement;
		try {
			statement = conn.prepareStatement(SELECT_CUR_CHAR_CREATED);
			statement.setString(1, playerName);

			ResultSet results = statement.executeQuery();

			if(!results.next()) {
				results.close();
				return null;
			}

			int curr = results.getInt("current_character_uid");
			int cre = results.getInt("created_characters_id");

			results.close();
			return new int[]{curr, cre};
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to get player information about " + playerName);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns a list of created characters from the specified id (
	 * which can be retrieved per-player name using getActive(playerName))
	 *
	 * @param createdId the id corresponding with the characters
	 * @return a list of created characters (potentially empty) or null if an error occurs
	 */
	public List<CreatedCharacterInfo> getCreatedCharacterInfo(int createdId) {
		try {
			PreparedStatement statement = conn.prepareStatement(SELECT_CREATED_CHARS);

			statement.setInt(1, createdId);

			ResultSet resultSet = statement.executeQuery();

			List<CreatedCharacterInfo> result = new ArrayList<>();

			while(resultSet.next()) {
				CreatedCharacterInfo cci = new CreatedCharacterInfo(createdId);
				cci.setCharUid(resultSet.getInt("char_uid"));
				cci.setCreatedUTC(resultSet.getLong("created_utc"));
				cci.setGenerationMethod(resultSet.getString("generation_method"));
				cci.setStoredUTC(resultSet.getLong("stored_utc"));
				result.add(cci);
			}

			resultSet.close();
			return result;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retrieve created character info for id " + createdId);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets the created character info corresponding to a characters uid
	 * @param charUid the uid
	 * @return the created character info
	 */
	public CreatedCharacterInfo getCreatedCharacterInfoByChar(int charUid) {
		try {
			PreparedStatement statement = conn.prepareStatement(SELECT_CREATED_CHAR_BY_UID);

			statement.setInt(1, charUid);

			ResultSet resultSet = statement.executeQuery();

			if(resultSet.next()) {
				CreatedCharacterInfo cci = new CreatedCharacterInfo(resultSet.getInt("id"));
				cci.setCharUid(resultSet.getInt("char_uid"));
				cci.setCreatedUTC(resultSet.getLong("created_utc"));
				cci.setGenerationMethod(resultSet.getString("generation_method"));
				cci.setStoredUTC(resultSet.getLong("stored_utc"));
				resultSet.close();
				return cci;
			}

			return null;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retrieve created character info for uid " + charUid);
			e.printStackTrace();
		}
		return null;
	}

	public boolean addOrUpdateCreatedCharacterInfo(CreatedCharacterInfo cci) {
		try {
			PreparedStatement statement = conn.prepareStatement(INSERT_OR_UPDATE_CREATED_CHAR);

			statement.setInt(1, cci.getId());
			statement.setInt(2, cci.getCharUid());
			statement.setLong(3, cci.getCreatedUTC());
			statement.setString(4, cci.getGenerationMethod());
			statement.setLong(5, cci.getStoredUTC());

			statement.execute();
			return true;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to add created character info " + cci);
			e.printStackTrace();
			return false;
		}
	}

	public boolean clearCreatedCharacterInfo(int id) {
		try {
			PreparedStatement statement = conn.prepareStatement(CLEAR_CREATED_CHARS);

			statement.setInt(1, id);

			statement.execute();
			return true;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retrieve clear created character info for id " + id);
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean inflictWound(GildorymCharacter gChar, Wound w) {
		try {
			PreparedStatement statement;
			if(gChar.getWoundsID() == -1) {
				// Assign a wound id
				statement = conn.prepareStatement("SELECT max(wound_id) FROM wounds");
				
				ResultSet resultSet = statement.executeQuery();
				resultSet.next();
				
				int max = resultSet.getInt(1);
				System.out.println("Assigned wound id " + (max + 1) + " to " + gChar.getName());
				resultSet.close();
				
				gChar.setWoundsID(max + 1);
				saveCharacter(gChar);
				w.setWoundID(max + 1);
				
			}else {
				w.setWoundID(gChar.getWoundsID());
			}
			
			statement = conn.prepareStatement(INSERT_WOUND);

			statement.setInt(1, w.getWoundID());
			statement.setLong(2, w.getTimestamp());
			statement.setString(3, w.getDamageType().name());
			statement.setInt(4, w.getDamageAmount());
			statement.setLong(5, w.getTimeRegen());
			statement.setInt(6, w.getPotionEffect());
			statement.setInt(7, w.getEffectLevel());
			statement.setString(8, w.getNotes());
			
			statement.execute();
			
			statement = conn.prepareStatement("SELECT LAST_INSERT_ID()");
			
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			int uid = resultSet.getInt(1);
			resultSet.close();
			w.setWoundUID(uid);
			return true;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to inflict wound " + w + " on " + gChar.getName() + " [" + gChar.getUid() + "]");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Removes any wounds that have healed from the gildorym character,
	 * and reloads the wounds. Can be used in replace of loadWounds if
	 * this behavior is preferred
	 * 
	 * @param gChar the character
	 * @return if the call was successful
	 */
	public boolean pruneWounds(GildorymCharacter gChar) {
		if(gChar.getWoundsID() == -1) {
			return true;
			
		}
		try {
			PreparedStatement statement;
			
			statement = conn.prepareStatement(PRUNE_WOUNDS_BY_ID);

			statement.setInt(1, gChar.getWoundsID());
			statement.setLong(2, System.currentTimeMillis());
			
			statement.execute();
			
			loadWounds(gChar);
			if(gChar.getWounds().isEmpty()) {
				gChar.setWoundsID(-1);
				saveCharacter(gChar);
			}
			return true;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to prune wounds on " + gChar.getName() + " [" + gChar.getUid() + "]");
			e.printStackTrace();
			return false;
		}
	}

	public boolean loadWounds(GildorymCharacter gChar) {
		gChar.clearWounds();
		if(gChar.getWoundsID() == -1)
			return true;
		
		try {
			PreparedStatement statement;
			
			statement = conn.prepareStatement(SELECT_WOUNDS_BY_ID);
			statement.setInt(1, gChar.getWoundsID());
			
			ResultSet resultSet = statement.executeQuery();
			
			Wound w;
			while(resultSet.next()) {
				w = new Wound(resultSet.getInt("wound_uid"));
				w.setWoundID(resultSet.getInt("wound_id"));
				w.setTimestamp(resultSet.getLong("timestamp"));
				w.setDamageType(DamageType.valueOf(resultSet.getString("damage_type")));
				w.setDamageAmount(resultSet.getInt("damage_amount"));
				w.setTimeRegen(resultSet.getLong("regen_time"));
				w.setPotionEffect(resultSet.getInt("potion_effect"));
				w.setPotionLevel(resultSet.getInt("effect_level"));
				w.setNotes(resultSet.getString("notes"));
				
				gChar.addWound(w);
			}
			return true;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to load wounds on " + gChar.getName() + " [" + gChar.getUid() + "]");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Deletes the wound from the character, if that was the last
	 * wound in the characters IN-MEMORY list of wounds, then the wounds
	 * on the character is reloaded from the database. If the list is
	 * still empty, the wound id is set to -1. Because this may take 3 
	 * db calls, its best to use clearWounds when sensible, rather than
	 * iteratively delete the mall
	 * 
	 * @param gChar The character
	 * @param w The wound
	 * @return whether the operation was successful or not
	 */
	public boolean deleteWound(GildorymCharacter gChar, Wound w) {
		try {
			PreparedStatement statement;
			
			statement = conn.prepareStatement(DELETE_WOUND);

			statement.setInt(1, w.getWoundUID());
			
			statement.execute();
			
			gChar.removeWound(w);
			
			if(gChar.getWounds().size() == 0) {
				loadWounds(gChar);
				
				if(gChar.getWounds().size() == 0) {
					gChar.setWoundsID(-1);
					saveCharacter(gChar);
				}
			}
			return true;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to prune wounds on " + gChar.getName() + " [" + gChar.getUid() + "]");
			e.printStackTrace();
			return false;
		}
	}

	public boolean saveStats(GildorymStats stats) {
		try {
			PreparedStatement statement;
			int position = 1;
			
			if(stats.getStatsUid() == -1) {
				statement = conn.prepareStatement(INSERT_STATS);
			}else {
				statement = conn.prepareStatement(REPLACE_STATS);
				statement.setInt(position++, stats.getStatsUid());
			}
			
			statement.setInt(position++, stats.getStamina());
			statement.setInt(position++, stats.getMagicalStamina());
			statement.setInt(position++, stats.getLockpickStamina());
			
			statement.execute();
			
			if(stats.getStatsUid() == -1) {
				statement = conn.prepareStatement("SELECT LAST_INSERT_ID()");
				ResultSet results = statement.executeQuery();
				
				results.next();
				stats.setStatsUid(results.getInt(1));
				results.close();
			}
			
			return true;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to save stats " + stats);
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Deletes the stats from the character, and sets the stats uid to -1.
	 * 
	 * @param gChar the character
	 * @return if the operation was probably successful
	 */
	public boolean deleteStats(GildorymCharacter gChar) {
		try {
			PreparedStatement statement;
			
			statement = conn.prepareStatement(DELETE_STATS);

			statement.setInt(1, gChar.getStats().getStatsUid());
			
			statement.execute();
			
			gChar.getStats().setStatsUid(-1); 
			return true;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to delete stats from " + gChar.getName() + " (" + gChar + ")");
			return false;
		}
	}
	
	public GildorymStats getStats(int statsUid) {
		try {
			PreparedStatement statement;
			
			statement = conn.prepareStatement(SELECT_STATS_BY_ID);
			
			statement.setInt(1, statsUid);
			
			ResultSet resultSet = statement.executeQuery();
			
			GildorymStats stats = new GildorymStats( 
					statsUid,
					resultSet.getInt("stamina"),
					resultSet.getInt("magical_stamina"),
					resultSet.getInt("lockpick_stamina")
					);
			
			resultSet.close();
			return stats;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retrieve stats on uid " + statsUid);
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean saveSpecialization(Specialization specialization) {
		try {
			PreparedStatement statement;
			int position = 1;
			
			if(specialization.getId() == -1) {
				statement = conn.prepareStatement(INSERT_SPECIALIZATION);
			}else {
				statement = conn.prepareStatement(REPLACE_SPECIALIZATION);
				statement.setInt(position++, specialization.getId());
			}
			
			statement.setInt(position++, specialization.getBaseAttackMod());
			statement.setInt(position++, specialization.getFortSave());
			statement.setInt(position++, specialization.getRefSave());
			statement.setInt(position++, specialization.getWillSave());
			statement.setInt(position++, specialization.getFeatId());
			
			statement.execute();
			
			if(specialization.getId() == -1) {
				statement = conn.prepareStatement("SELECT LAST_INSERT_ID()");
				ResultSet results = statement.executeQuery();
				
				results.next();
				specialization.setId(results.getInt(1));
				results.close();
			}
			
			return true;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to save specialization " + specialization);
			e.printStackTrace();
			return false;
		}
	}

	private Specialization getSpecialization(int id) {
		try {
			PreparedStatement statement;
			
			statement = conn.prepareStatement(SELECT_SPECIALIZATION_BY_ID);
			statement.setInt(1, id);
			
			ResultSet rSet = statement.executeQuery();
			
			if(!rSet.next())
				return null;
			
			Specialization result = new Specialization(rSet.getInt("specialization_id"),
					rSet.getInt("child_specialization"), 
					rSet.getInt("base_attack"),
					rSet.getInt("fort_save"),
					rSet.getInt("ref_save"),
					rSet.getInt("will_save"),
					rSet.getInt("feat_id"));
			rSet.close();
			return result;
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retrieve specialization (id=" + id + ")");
			e.printStackTrace();
		}
		return null;
	}
}