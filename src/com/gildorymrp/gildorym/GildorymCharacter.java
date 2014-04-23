package com.gildorymrp.gildorym;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gildorymrp.charactercards.CharacterCard;
import com.gildorymrp.gildorymclasses.CharacterClass;
import com.gildorymrp.gildorymclasses.CharacterProfession;

/**
 * Everything that describes a character in gildorym
 * 
 * @author Timothy
 */
public class GildorymCharacter {
	private int uid;
	private String name;
	private String mcName;
	private CharacterCard charCard;
	private CharacterProfession profession1;
	private CharacterProfession profession2;
	private Specialization specialization;
	private CharacterClass charClass;
	private String deity;
	private int birthday; // TODO: GildorymTime
	private int level;
	private int experience;
	private GildorymStats stats;
	private double x, y, z;
	private String world;
	private int woundsID;
	private List<Wound> wounds;
	
	public GildorymCharacter(int uid) {
		this.uid = uid;
		woundsID = -1;
		wounds = new ArrayList<>();
	}
	
	public GildorymCharacter(int uid, String name, String mcName,
			CharacterCard charCard, CharacterProfession profession1,
			CharacterProfession profession2, Specialization specialization, 
			CharacterClass charClass, String deity, int birthday, int level, 
			int experience, GildorymStats stats, double x, double y, double z,
			String world, int woundsID, List<Wound> wounds) {
		this.uid = uid;
		this.name = name;
		this.mcName = mcName;
		this.charCard = charCard;
		this.profession1 = profession1;
		this.profession2 = profession2;
		this.specialization = specialization;
		this.charClass = charClass;
		this.deity = deity;
		this.birthday = birthday;
		this.level = level;
		this.experience = experience;
		this.stats = stats;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.woundsID = woundsID;
		this.wounds = wounds;
	}

	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMcName() {
		return mcName;
	}
	public void setMcName(String mcName) {
		this.mcName = mcName;
	}
	public CharacterCard getCharCard() {
		return charCard;
	}
	public void setCharCard(CharacterCard charCard) {
		this.charCard = charCard;
	}
	public CharacterProfession getProfession1() {
		return profession1;
	}
	public CharacterProfession getProfession2() {
		return profession2;
	}
	public Specialization getSpecialization() {
		return specialization;
	}
	public void setProfession1(CharacterProfession profession1) {
		this.profession1 = profession1;
	}
	public void setProfession2(CharacterProfession profession2) {
		this.profession2 = profession2;
	}
	public void setSpecialization(Specialization specialization) {
		this.specialization = specialization;
	}
	public CharacterClass getCharClass() {
		return charClass;
	}
	public void setCharClass(CharacterClass charClass) {
		this.charClass = charClass;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getExperience() {
		return experience;
	}
	public void setExperience(int experience) {
		this.experience = experience;
	}
	public GildorymStats getStats() {
		return stats;
	}

	public void setStats(GildorymStats stats) {
		this.stats = stats;
	}

	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
	
	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public int getWoundsID() {
		return woundsID;
	}
	
	public void setWoundsID(int woundsID) {
		this.woundsID = woundsID;
	}

	public void setWounds(Collection<Wound> wounds) {
		clearWounds();
		addAllWounds(wounds);
	}
	
	public void addAllWounds(Collection<Wound> wounds) {
		this.wounds.addAll(wounds);
	}

	public void addWound(Wound w) {
		wounds.add(w);
	}
	
	public void removeWound(Wound w) {
		this.wounds.remove(w);
	}
	
	public void clearWounds() {
		this.wounds.clear();
	}

	public List<Wound> getWounds() {
		return wounds;
	}

	public String getDeity() {
		return deity;
	}

	public void setDeity(String deity) {
		this.deity = deity;
	}

	public int getBirthday() {
		return birthday;
	}

	public void setBirthday(int birthday) {
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		return "GildorymCharacter [uid=" + uid + ", name=" + name + ", mcName="
				+ mcName + ", charCard=" + charCard + ", profession1="
				+ profession1 + ", profession2=" + profession2 + ", specializationId=" 
				+ specialization + "charClass=" + charClass + ", deity=" + deity
				+ ", birthday=" + birthday + ", level=" + level + ", experience="
				+ experience + ", stats=" + stats + ", x=" + x + ", y=" + y + ", z=" + z
				+ ", world=" + world + ", woundsID=" + woundsID + ", wounds="
				+ wounds + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GildorymCharacter other = (GildorymCharacter) obj;
		if (birthday != other.birthday)
			return false;
		if (charCard == null) {
			if (other.charCard != null)
				return false;
		} else if (!charCard.equals(other.charCard))
			return false;
		if (charClass != other.charClass)
			return false;
		if (deity == null) {
			if (other.deity != null)
				return false;
		} else if (!deity.equals(other.deity))
			return false;
		if (experience != other.experience)
			return false;
		if (level != other.level)
			return false;
		if (mcName == null) {
			if (other.mcName != null)
				return false;
		} else if (!mcName.equals(other.mcName))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (profession1 != other.profession1)
			return false;
		if (profession2 != other.profession2)
			return false;
		if(specialization == null) {
			if(other.specialization != null) {
				return false;
			}
		} else if(!specialization.equals(other.specialization)) {
			return false;
		}
		if (stats == null) {
			if (other.stats != null)
				return false;
		} else if (!stats.equals(other.stats))
			return false;
		if (uid != other.uid)
			return false;
		if (world == null) {
			if (other.world != null)
				return false;
		} else if (!world.equals(other.world))
			return false;
		if (wounds == null) {
			if (other.wounds != null)
				return false;
		} else if (!wounds.equals(other.wounds))
			return false;
		if (woundsID != other.woundsID)
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}
}