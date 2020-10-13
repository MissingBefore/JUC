package com.scarborough.nas.pojo;



public class User {
	private Integer id;
	private String name;
	private Integer phone;
	private String car_plate;
	private String car_mileage;//里程
	private String car_insurance;
	private String car_model;
	private String remarks;
	public User(Integer id, String name, Integer phone, String car_plate, String car_mileage, String car_insurance,
			String car_model, String remarks) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.car_plate = car_plate;
		this.car_mileage = car_mileage;
		this.car_insurance = car_insurance;
		this.car_model = car_model;
		this.remarks = remarks;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getPhone() {
		return phone;
	}
	public void setPhone(Integer phone) {
		this.phone = phone;
	}
	public String getCar_plate() {
		return car_plate;
	}
	public void setCar_plate(String car_plate) {
		this.car_plate = car_plate;
	}
	public String getCar_mileage() {
		return car_mileage;
	}
	public void setCar_mileage(String car_mileage) {
		this.car_mileage = car_mileage;
	}
	public String getCar_insurance() {
		return car_insurance;
	}
	public void setCar_insurance(String car_insurance) {
		this.car_insurance = car_insurance;
	}
	public String getCar_model() {
		return car_model;
	}
	public void setCar_model(String car_model) {
		this.car_model = car_model;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((car_insurance == null) ? 0 : car_insurance.hashCode());
		result = prime * result + ((car_mileage == null) ? 0 : car_mileage.hashCode());
		result = prime * result + ((car_model == null) ? 0 : car_model.hashCode());
		result = prime * result + ((car_plate == null) ? 0 : car_plate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (car_insurance == null) {
			if (other.car_insurance != null)
				return false;
		} else if (!car_insurance.equals(other.car_insurance))
			return false;
		if (car_mileage == null) {
			if (other.car_mileage != null)
				return false;
		} else if (!car_mileage.equals(other.car_mileage))
			return false;
		if (car_model == null) {
			if (other.car_model != null)
				return false;
		} else if (!car_model.equals(other.car_model))
			return false;
		if (car_plate == null) {
			if (other.car_plate != null)
				return false;
		} else if (!car_plate.equals(other.car_plate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		return true;
	}
	
}
