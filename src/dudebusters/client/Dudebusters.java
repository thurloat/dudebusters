package dudebusters.client;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Dudebusters implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * This is the entry point method.
	 */
	private Double x_mov = (double) 0;
	private Double y_mov = (double) 0;
	private Integer REFRESH_RATE = 25;
	private Integer MOVEMENT_RATE = 5;
	private Integer JUMP_RATE = 30;
	private Integer MAX_SPEED = 13;
	private Double ACCEL_RATE = 1.5;
	private Double GROUND_FRICTION = 1.15;
	private Double GRAVITY = -15.0;

	private Integer mouse_x = 0;
	private Integer mouse_y = 0;
	private Double mouse_angle = 0.0;
	private Double mouse_slope = 0.0;
	private Integer ground = 0;
	private Integer top_bound = 0;
	private Integer left_bound = 0;
	private Integer right_bound = 0;
	private HashSet<Bullet> bullets = new HashSet<Bullet>();

	public void onModuleLoad() {
		// Create Debugging junk
		final Button toggleRendering = new Button("Kill Switch");
		final Button startSwitch = new Button("Start Switch");
		final TextBox nameField = new TextBox();
		final Label errorLabel = new Label();

		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(toggleRendering);
		RootPanel.get("sendButtonContainer").add(startSwitch);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Set bounding for our world
		ground = Window.getClientHeight();
		right_bound = Window.getClientWidth();

		// Create our man!
		final Man man = new Man();

		// Add our man to the world
		RootPanel.get().add(man);

		// Place our man above the ground so he can drop in
		man.setYPos(this.ground - 250.0);

		// Track Mouse movement so we can follow it :)
		// The Native Preview Handler captures a 'preview' of each event
		// each even comes with it a mouse_x and mouse_y.
		// especially on mouse moves :P
		// if they arent passed, we ignore them and keep the previous value
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				NativeEvent e = event.getNativeEvent();
				try {
					if (e.getClientX() != 0 && e.getClientY() != 0) {
						mouse_x = e.getClientX();
						mouse_y = e.getClientY();
					}
				} catch (Exception err) {
					GWT.log(err.getMessage());
				}
			}
		});

		// Bind keyboard keypresses to the nameField textbox.
		// a = left movement
		// d = right movement
		// space = jump
		// s = shoot
		nameField.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				String code = event.getCharCode() + "";
				if (code.equals("a")) {
					if (x_mov > -MAX_SPEED) {
						x_mov -= MOVEMENT_RATE * ACCEL_RATE;
					} else {
						x_mov -= MOVEMENT_RATE;
					}
				} else if (code.equals("d")) {
					if (x_mov < MAX_SPEED) {
						x_mov += MOVEMENT_RATE * ACCEL_RATE;
					} else {
						x_mov += MOVEMENT_RATE;
					}
				} else if (code.equals(" ")) {
					if (x_mov > 5) {
						y_mov += JUMP_RATE * 1.25;
					}
					y_mov += JUMP_RATE;
				} else if (code.equals("s")) {
					Bullet new_bullet = new Bullet((double)man.getWeaponXPos(), (double) man.getWeaponYPos(),
							mouse_angle);
					RootPanel.get().add(new_bullet);
					bullets.add(new_bullet);
				}
			}
		});
		
		// GWT Timer that is scheduled to re-render everything
		final Timer t = new Timer() {
			@Override
			public void run() {
				if (man.getElement() != null) {
					// Man's new X&Y Position, plus how far he is moving in each
					Double new_x = man.getXPos() + x_mov;
					// since we're dealing with upsidedown Y axis, subtract.
					Double new_y = man.getYPos() - y_mov;

					// Check to make sure our new X position is within the world boundaries
					if (new_x >= right_bound) {
						new_x = right_bound - 30.0;
					} else if (new_x <= left_bound) {
						new_x = left_bound + 30.0;
					}
					// bounding Y to world
					if (new_y >= (ground - 100)) {
						new_y = (double) (ground - 80);
					} else if (new_y <= top_bound) {
						new_y = top_bound + 2.0;
					}
					
					// Assuming everything is good. set his X & Y!
					man.setXPos(new_x);
					man.setYPos(new_y);

					// Re-calulate velocity for next time.
					// Dont bother if we're not moving yet
					if (!x_mov.equals(0)) {
						// only apply friction if man is grounded
						if (y_mov == 0) {
							// friction
							x_mov = x_mov / GROUND_FRICTION;
							// if we're going reaaaally slow, stop.
							if (x_mov < 1 && x_mov > -1) {
									x_mov = 0.0;
							}
						}
					}

					// don't bother to re-calculate man's Y if he's not moving
					if (!y_mov.equals(0)) {
						// if man is jumping
						if (y_mov > 1) {
							// slow his role until less than 1
							y_mov = (y_mov / 1.4) - 1;
						} else {
							// fall at instant terminal velocity or hit ground.
							if (new_y == (ground - 80)) {
								y_mov = 0.0;
							} else {
								y_mov = GRAVITY;
							}
						}
					}
					// Weapon Rotation
					// Calculate the opposite and adjacent sides of the triangle
					// that the gun barrel and the mouse pointer would form
					Double diff_x = (double) (mouse_x - man.getWeaponXPos());
					Double diff_y = (double) (man.getWeaponYPos() - mouse_y);
					
					// Angle of said triangle, and the potential trajectory for the
					// bullet is the arctan of Opposite / Adjacent
					// This will give us a crappy / inaccurate radian number
					mouse_angle = Math.atan(diff_y / diff_x);

					// Since the browser and all libraries think angles start on the
					// left side of the screen, we need to shift the calculation around
					// to make it work everywhere else.
					if (diff_x >= 0 && diff_y <= 0) {
						// +,-
						// add 2PI
						mouse_angle = mouse_angle + (2 * Math.PI);
					} else if (diff_x > 0 && diff_y > 0) {
						// +,+
						// leave it
					} else if (diff_x <= 0 && diff_y >= 0) {
						// -,+
						// add PI
						mouse_angle = mouse_angle + Math.PI;
					} else if (diff_x <= 0 && diff_y <= 0) {
						// -,-
						// add a whole PI
						mouse_angle = mouse_angle + Math.PI;
					}
					//apply rotation!
					man.setWeaponRotation(mouse_angle);

					errorLabel.setText("Accel X:" + x_mov + " Accel Y:" + y_mov
							+ "| MouseX,Y(" + mouse_x + "," + mouse_y
							+ ") ManX,Y(" + man.getXPos() + "," + man.getYPos()
							+ "), Mouse Angle(" + mouse_angle + ")");
					/*
					 * Render Bullets, if any
					 */

					if (bullets != null) {
						if (!bullets.isEmpty()) {
							for (Bullet bullet : bullets) {
								if (bullet != null) {
									bullet.draw();
								}
							}
							for (Bullet bullet : bullets) {
								if (bullet.getAbsoluteLeft() <= left_bound
										|| bullet.getAbsoluteLeft() >= (right_bound - 15)
										|| bullet.getAbsoluteTop() <= top_bound
										|| bullet.getAbsoluteTop() >= ground) {
									bullet.removeFromParent();
									bullets.remove(bullet);
									bullet = null;
								}
							}
						}
					}

				}

			}

		};

		t.scheduleRepeating(REFRESH_RATE);
		toggleRendering.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				t.cancel();
			}
		});
		startSwitch.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				t.scheduleRepeating(REFRESH_RATE);
			}
		});

	}
}