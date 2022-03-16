package net.yogstation.yogbot.permissions;

import java.util.HashMap;
import java.util.Map;

public class PermissionsManager {
	private final Map<String, PermissionsNode> nodes = new HashMap<>();

	public PermissionsManager() {
		addNode(new PermissionsNodeBuilder()
				.setName("Wiki Staff")
				.setPerms("wikiban")
				.build(this));

		addNode(new PermissionsNodeBuilder()
				.setName("Lore Team")
				.setPerms("loreban")
				.build(this));

		addNode(new PermissionsNodeBuilder()
				.setName("subscriber")
				.setPerms("unsubscribe")
				.build(this));

		addNode(new PermissionsNodeBuilder()
				.setName("YogPost")
				.setPerms("post")
				.build(this));

		addNode(new PermissionsNodeBuilder()
				.setName("mentor")
				.setPerms("mehlp", "listmentors")
				.build(this));

		addNode(new PermissionsNodeBuilder()
				.setName("Head Mentor")
				.setPerms("addmentor", "removementor", "mentorban")
				.setParents("mentor")
				.build(this));

		addNode(new PermissionsNodeBuilder()
				.setName("maintainer")
				.setPerms("reboot", "toggleooc")
				.setParents("mentor")
				.build(this));

		addNode(new PermissionsNodeBuilder()
				.setName("retmin")
				.setPerms("tempban", "ban", "unban", "kick", "ticket", "whitelist", "note", "staffban", "listadmins")
				.setParents("maintainer")
				.build(this));

		addNode(new PermissionsNodeBuilder()
				.setName("staff")
				.setParents("retmin")
				.build(this));

		addNode(new PermissionsNodeBuilder()
				.setName("senior admin")
				.setPerms("addao", "removeao", "addmentor", "removementor")
				.setParents("staff")
				.build(this));

		addNode(new PermissionsNodeBuilder()
				.setName("head-developer")
				.setParents("senior admin")
				.build(this));

		addNode(new PermissionsNodeBuilder()
				.setName("council")
				.setPerms("userverify")
				.setParents("head-developer")
				.build(this));

		addNode(new PermissionsNodeBuilder()
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
}

class PermissionsNodeBuilder {
	private String name = "";
	private String[] perms = new String[0];
	private String[] parents = new String[0];

	public PermissionsNodeBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public PermissionsNodeBuilder setPerms(String... perms) {
		this.perms = perms;
		return this;
	}

	public PermissionsNodeBuilder setParents(String... parents) {
		this.parents = parents;
		return this;
	}

	public PermissionsNode build(PermissionsManager manager) {
		PermissionsNode node = new PermissionsNode(name);
		node.addPermissions(perms);

		for(String string : parents) {
			PermissionsNode parentNode = manager.getNodeFor(string);
			assert parentNode != null;
			node.addParents(parentNode);
		}

		return node;
	}
}