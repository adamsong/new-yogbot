package net.yogstation.yogbot.permissions;

import discord4j.core.object.entity.Member;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PermissionsManager {
	private final Map<String, PermissionsNode> nodes = new HashMap<>();

	public PermissionsManager() {
		addNode(PermissionsNode.builder()
				.setName("Wiki Staff")
				.setPerms("wikiban")
				.build(this));

		addNode(PermissionsNode.builder()
				.setName("Lore Team")
				.setPerms("loreban")
				.build(this));

		addNode(PermissionsNode.builder()
				.setName("subscriber")
				.setPerms("unsubscribe")
				.build(this));

		addNode(PermissionsNode.builder()
				.setName("YogPost")
				.setPerms("post")
				.build(this));

		addNode(PermissionsNode.builder()
				.setName("mentor")
				.setPerms("mehlp", "listmentors")
				.build(this));

		addNode(PermissionsNode.builder()
				.setName("Head Mentor")
				.setPerms("addmentor", "removementor", "mentorban")
				.setParents("mentor")
				.build(this));

		addNode(PermissionsNode.builder()
				.setName("maintainer")
				.setPerms("reboot", "toggleooc")
				.setParents("mentor")
				.build(this));

		addNode(PermissionsNode.builder()
				.setName("retmin")
				.setPerms("tempban", "ban", "unban", "kick", "ticket", "whitelist", "note", "staffban", "listadmins")
				.setParents("maintainer")
				.build(this));

		addNode(PermissionsNode.builder()
				.setName("staff")
				.setParents("retmin")
				.build(this));

		addNode(PermissionsNode.builder()
				.setName("senior admin")
				.setPerms("addao", "removeao", "addmentor", "removementor")
				.setParents("staff")
				.build(this));

		addNode(PermissionsNode.builder()
				.setName("head-developer")
				.setParents("senior admin")
				.build(this));

		addNode(PermissionsNode.builder()
				.setName("council")
				.setPerms("userverify")
				.setParents("head-developer")
				.build(this));

		addNode(PermissionsNode.builder()
				.setName("host")
				.setParents("council")
				.build(this));

		nodes.keySet().forEach(key -> nodes.get(key).calculatePermissions());
	}

	public PermissionsNode getNodeFor(String name) {
		return nodes.getOrDefault(name, null);
	}

	public void addNode(PermissionsNode node) {
		nodes.put(node.getRole(), node);
	}

	/**
	 * Checks if the application command is being run by someone authorized to run the command
	 * @param member The member
	 * @return If the member has permission
	 */
	public boolean hasPermission(Member member, String requiredPermission) {
		if(member == null) return false;

		return member.getRoles()
			.any(role -> {
				PermissionsNode node = getNodeFor(role.getName());
				if(node == null) return false;
				return node.hasPermission(requiredPermission);
			})
			.block() == Boolean.TRUE; // == Boolean.TRUE prevents NPE
	}
	
	
}
