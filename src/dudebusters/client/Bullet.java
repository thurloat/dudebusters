/**
 * 
 */
package dudebusters.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author thurloat
 *
 */
public class Bullet extends Composite {
	
	public Double current_x = 0.0;
	public Double current_y = 0.0;
	
	public Double angle = 0.0;
	public Double velocity = 0.0;
	public Double mass = 200.0;
	public Double speed = 8.0;
	
	public HTML bullet_element = new HTML();
	
	public Bullet(Double start_x, Double start_y, Double angle){
		this.current_x = start_x;
		this.current_y = start_y;
		
		this.angle = angle;
		
		bullet_element.setStyleName("bullet");
		bullet_element.getElement().getStyle().setTop(this.current_y, Style.Unit.PX);
		bullet_element.getElement().getStyle().setLeft(this.current_x, Style.Unit.PX);
		this.initWidget(bullet_element);
		
	}
	public void draw(){
		Double distance_x = speed * Math.cos(-1 * angle);
		Double distance_y = speed * Math.sin(-1 * angle);
//		GWT.log(angle + "");
		current_x = current_x + distance_x;
		current_y = current_y + distance_y;
		bullet_element.getElement().getStyle().setLeft(current_x, Style.Unit.PX);
		bullet_element.getElement().getStyle().setTop(current_y, Style.Unit.PX);
	}
}