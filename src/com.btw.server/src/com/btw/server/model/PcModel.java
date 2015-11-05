package com.btw.server.model;

import java.sql.Timestamp;

public class PcModel {

	  private int id;
	  private String server_name;
	  private String server_ip;
	  private Timestamp created;
	  private Timestamp updated;

	  public int getId()
	  {
	    return this.id;
	  }
	  public void setId(int id) {
	    this.id = id;
	  }
	  public String getServer_name() {
	    return this.server_name;
	  }
	  public void setServer_name(String server_name) {
	    this.server_name = server_name;
	  }
	  public String getServer_ip() {
	    return this.server_ip;
	  }
	  public void setServer_ip(String server_ip) {
	    this.server_ip = server_ip;
	  }
	  public Timestamp getCreated() {
	    return this.created;
	  }
	  public void setCreated(Timestamp created) {
	    this.created = created;
	  }
	  public Timestamp getUpdated() {
	    return this.updated;
	  }
	  public void setUpdated(Timestamp updated) {
	    this.updated = updated;
	  }
}
