package com.dumbdogdiner.challenges;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.dumbdogdiner.challenges.utils.Util;

/**
 * Structure representing a challenge players can complete.
 */
public class Challenge {

	/**
	 * An array of all active challenges.
	 */
	public static ArrayList<Challenge> challenges = new ArrayList<Challenge>();

	/**
	 * Fetch challenges by name.
	 */
	public static Challenge getChallengeByName(String name) {
		for (Challenge challenge : Challenge.challenges) {
			if (challenge.getChallengeName().equals(name)) {
				return challenge;
			}
		}
		return null;
	}

	/**
	 * Create random challenges.
	 */
	public static ArrayList<Challenge> getRandomChallenges(int amountOfRandomChallenges) {
		ArrayList<Challenge> challenges = new ArrayList<Challenge>();
		if (Challenge.challenges.size() < 5) {
			amountOfRandomChallenges = Challenge.challenges.size();
		}
		for (int i = 0; i < amountOfRandomChallenges; i++) {
			int randInt = Util.randInt(0, Challenge.challenges.size() - 1);
			Challenge challenge = Challenge.challenges.get(randInt);
			if (challenges.contains(challenge)) {
				i = i - 1;
				continue;
			}
			challenges.add(new Challenge(challenge.challengeName, challenge.challengeType,
					challenge.objectiveObjectTypes, challenge.counters));
		}
		return challenges;
	}

	/**
	 * The name of this challenge.
	 */
	private String challengeName;

	/**
	 * The type of this challenge.
	 */
	private ChallengeType challengeType;

	/**
	 * Challenge objectives.
	 */
	private List<String> objectiveObjectTypes;

	/**
	 * Hash map of user counters.
	 */
	private LinkedHashMap<String, Integer> counters;

	public Challenge(String challengeName, ChallengeType challengeType, List<String> objectiveObjectTypes,
			LinkedHashMap<String, Integer> counters) {
		this.challengeName = challengeName;
		this.challengeType = challengeType;
		this.objectiveObjectTypes = objectiveObjectTypes;
		this.counters = counters;
	}

	/**
	 * Get the name of this challenge.
	 */
	public String getChallengeName() {
		return challengeName;
	}

	/**
	 * Get the type of this challenge.
	 */
	public ChallengeType getChallengeType() {
		return challengeType;
	}

	/**
	 * Get the objective object types of this challenge.
	 */
	public List<String> getObjectiveObjectTypes() {
		return objectiveObjectTypes;
	}

	/**
	 * Fetch the player counter map of this challenge.
	 */
	public LinkedHashMap<String, Integer> getCounters() {
		return counters;
	}

	/**
	 * Set this challenge's name.
	 */
	public void setChallengeName(String challengeName) {
		this.challengeName = challengeName;
	}

	/**
	 * Set this challenge's type.
	 */
	public void setChallengeType(ChallengeType challengeType) {
		this.challengeType = challengeType;
	}

	/**
	 * Set the challenge's objectives.
	 */
	public void setObjectiveObjectType(ArrayList<String> objectiveObjects) {
		this.objectiveObjectTypes = objectiveObjects;
	}

	/**
	 * Set the type of the challenge's objectives.
	 */
	public void setObjectiveObjectTypes(ArrayList<String> objectiveObjectTypes) {
		this.objectiveObjectTypes = objectiveObjectTypes;
	}

	public void setCounters(LinkedHashMap<String, Integer> counters) {
		this.counters = counters;
	}

	/**
	 * Get the ranking of a player.
	 */
	public Integer getRanking(String playerName) {
		int ranking = 1;
		for (String key : this.counters.keySet()) {
			if (key.equals(playerName)) {
				return ranking;
			}
			ranking++;
		}
		return -1;
	}

	/**
	 * Update this challenge's counter.
	 * 
	 * @param key
	 * @param amount
	 */
	public void updateCounter(String key, int amount) {
		if (this.counters.containsKey(key)) {
			this.counters.put(key, this.counters.get(key) + amount);
		} else {
			this.counters.put(key, 0);
		}
	}
}
