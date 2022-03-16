package net.yogstation.yogbot.permissions;

import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PermissionsNode {
	/**
	 * The set of permissions given to this node
	 */
	private final Set<String> permissions = new HashSet<>();

	/**
	 * The parents of this node
	 */
	private final Set<PermissionsNode> parents = new HashSet<>();

	/**
	 * If finalize has been called on this node yet
	 * Indicates this node contains the full hierarchy of permissions
	 * Prevents adding new permissions
	 */
	private boolean finalized = false;

	private final String role;

	/**
	 * Creates a permissions node for the specified role
	 * @param role The role name
	 */
	public PermissionsNode(String role) {
		this.role = role;
	}

	/**
	 * Gets the name of the role this permissions node is for
	 * @return The name of the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Gets the set of permissions for this node.
	 * @return The permissions
	 */
	public Set<String> getPermissions() {
		return permissions;
	}

	/**
	 * Adds permissions nodes
	 * @param perms The perms
	 */
	public void addPermissions(String... perms) {
		permissions.addAll(Arrays.stream(perms).toList());
	}

	/**
	 * Checks if this permission node has the specified permission
	 * @param perm The permission
	 * @return If this node has it
	 */
	public boolean hasPermission(String perm) {
		return permissions.contains(perm);
	}

	/**
	 * Adds parents to this node
	 * @param parents The parents of this node
	 */
	public void addParents(PermissionsNode... parents) {
		this.parents.addAll(Arrays.stream(parents).toList());
	}

	/**
	 * Calculates the permissions of this node using the parents
	 * Sets finalized to avoid recalculating
	 */
	public void calculatePermissions() {
		if(finalized) return;
		finalized = true;

		for(PermissionsNode parent : parents) {
			parent.calculatePermissions();
			permissions.addAll(parent.getPermissions());
		}
	}
}
