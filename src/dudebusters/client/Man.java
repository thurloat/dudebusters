/**
 * 
 */
package dudebusters.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author thurloat
 * 
 */
public class Man extends Composite {

	/**
	 * 
	 */
	public FlowPanel body = new FlowPanel();
	public HTML weapon = new HTML();

	public Man() {
		this.body.addStyleName("man");
		this.weapon.addStyleName("weapon");

		this.body.add(weapon);
		this.initWidget(body);
		// TODO Auto-generated constructor stub
	}

	/*
	 * Shortcut for setting the style TOP value in PX
	 */
	public void setYPos(Double top_px) {
		this.getElement().getStyle().setTop(top_px, Style.Unit.PX);
	}

	/*
	 * Shortcut to get Y Position of man. Remember that Y-Axis is inverted
	 */
	public Integer getYPos() {
		return this.getAbsoluteTop();
	}

	public void setXPos(Double left_px) {
		this.getElement().getStyle().setLeft(left_px, Style.Unit.PX);
	}

	/*
	 * Shortcut for getting his X-position.
	 */
	public Integer getXPos() {
		return this.getAbsoluteLeft();
	}

	/*
	 * Shortcut method to standardize how to get the weapon's Y Position
	 */
	public Integer getWeaponYPos() {
		return this.weapon.getAbsoluteTop() + 10;
	}

	/*
	 * Shortcut method to standardize how to get the weapon's X Position
	 */
	public Integer getWeaponXPos() {
		return this.weapon.getAbsoluteLeft() + 25;
	}
	
	public void setWeaponRotation(Double radians){
		this.rotate_el(this.weapon.getElement(), radians);
	}
	/*
	 * Browsers appear to take an inverse radian to get the rotation right
	 */
	public native void rotate_el(Element el, Double radians) /*-{
		var rotate_func = "rotate(-" + radians + "rad)";
		el.style.webkitTransform = rotate_func;
		el.style.MozTransform = rotate_func;
		el.style.transform = rotate_func;
	}-*/;

}
