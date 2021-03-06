package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ItemModel {

	private Long identify;
	private String long_title;
	private String url;
	private String wapurl;
	private String img_url;
	private BigDecimal price;
	private BigDecimal price_new;
	private BigDecimal cheap;
	private String site;
	private String site_url;
	private Long tagid;
	private String tag;
	private Integer post;
	private Integer status;
	private String category_code;
	private String category_name;
	private Timestamp created;
	private Timestamp updated;
	private Long sort;

	public Long getIdentify() {
		return identify;
	}
	public void setIdentify(Long identify) {
		this.identify = identify;
	}
	public String getLong_title() {
		return long_title;
	}
	public void setLong_title(String long_title) {
		this.long_title = long_title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getWapurl() {
		return wapurl;
	}
	public void setWapurl(String wapurl) {
		this.wapurl = wapurl;
	}
	public String getImg_url() {
		return img_url;
	}
	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getPrice_new() {
		return price_new;
	}
	public void setPrice_new(BigDecimal price_new) {
		this.price_new = price_new;
	}
	public BigDecimal getCheap() {
		return cheap;
	}
	public void setCheap(BigDecimal cheap) {
		this.cheap = cheap;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getSite_url() {
		return site_url;
	}
	public void setSite_url(String site_url) {
		this.site_url = site_url;
	}
	public Long getTagid() {
		return tagid;
	}
	public void setTagid(Long tagid) {
		this.tagid = tagid;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Integer getPost() {
		return post;
	}
	public void setPost(Integer post) {
		this.post = post;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getCategory_code() {
		return category_code;
	}
	public void setCategory_code(String category_code) {
		this.category_code = category_code;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	public Timestamp getUpdated() {
		return updated;
	}
	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}
	public Long getSort() {
		return sort;
	}
	public void setSort(Long sort) {
		this.sort = sort;
	}
}
