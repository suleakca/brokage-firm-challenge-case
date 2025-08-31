package com.brokerage.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "assets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"customerId", "assetName"})
})

public class Asset {
	
    public Asset() {

	}
	
    public Asset(Long id, String customerId, String assetName, double size, double usableSize) {
		super();
		this.id = id;
		this.customerId = customerId;
		this.assetName = assetName;
		this.size = size;
		this.usableSize = usableSize;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public double getUsableSize() {
		return usableSize;
	}

	public void setUsableSize(double usableSize) {
		this.usableSize = usableSize;
	}

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String assetName;

    @Column(nullable = false)
    private double size = 0.0;

    @Column(nullable = false)
    private double usableSize = 0.0;
}

