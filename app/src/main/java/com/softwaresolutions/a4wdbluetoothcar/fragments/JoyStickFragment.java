
package com.softwaresolutions.a4wdbluetoothcar.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softwaresolutions.a4wdbluetoothcar.R;
import com.softwaresolutions.a4wdbluetoothcar.activities.MainActivity;
import com.softwaresolutions.a4wdbluetoothcar.generated.JoyStick;

public class JoyStickFragment extends Fragment {

    private RelativeLayout layout_joystick;
    private TextView textView1, textView2, textView3, textView4, textView5;
    private RadioGroup joyGearsGroup;

    private JoyStick js;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.joystick_fragment, container, false);
        textView1 = (TextView) v.findViewById(R.id.textView1);
        textView2 = (TextView) v.findViewById(R.id.textView2);
        textView3 = (TextView) v.findViewById(R.id.textView3);
        textView4 = (TextView) v.findViewById(R.id.textView4);
        textView5 = (TextView) v.findViewById(R.id.textView5);

        joyGearsGroup = (RadioGroup) v.findViewById(R.id.joy_gears_group);

        layout_joystick = (RelativeLayout) v.findViewById(R.id.layout_joystick);

        js = new JoyStick(getContext().getApplicationContext(), layout_joystick, R.drawable.image_button);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);

                int speed = (int) Math.min(js.getDistance(), js.getLayoutWidth() / 2) * 100 / (js.getLayoutWidth() / 2);
                int speedGear = (speed + 19) / 20;

                String gear;
                if (speedGear == 1) {
                    gear = MainActivity.GEAR_1;
                    joyGearsGroup.check(R.id.joy_gear_one);
                } else if (speedGear == 2) {
                    gear = MainActivity.GEAR_2;
                    joyGearsGroup.check(R.id.joy_gear_two);
                } else if (speedGear == 3) {
                    gear = MainActivity.GEAR_3;
                    joyGearsGroup.check(R.id.joy_gear_three);
                } else if (speedGear == 4) {
                    gear = MainActivity.GEAR_4;
                    joyGearsGroup.check(R.id.joy_gear_four);
                } else if (speedGear == 5) {
                    gear = MainActivity.GEAR_5;
                    joyGearsGroup.check(R.id.joy_gear_five);
                } else {
                    gear = MainActivity.STOP;
                    joyGearsGroup.clearCheck();
                }
                ((MainActivity) getActivity()).sendData(gear);

                String command = MainActivity.STOP;
                if (arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    textView1.setText("X : " + String.valueOf(js.getX()));
                    textView2.setText("Y : " + String.valueOf(js.getY()));
                    textView3.setText("Angle : " + String.valueOf(js.getAngle()));
                    textView4.setText("Distance : " + String.valueOf(js.getDistance()));

                    int direction = js.get8Direction();

                    if (direction == JoyStick.STICK_UP) {
                        textView5.setText("Direction : Up");
                        command = MainActivity.FORWARD;
                    } else if (direction == JoyStick.STICK_UPRIGHT) {
                        textView5.setText("Direction : Up Right");
                        command = MainActivity.FORWARD_RIGHT;
                    } else if (direction == JoyStick.STICK_RIGHT) {
                        textView5.setText("Direction : Right");
                        command = MainActivity.RIGHT;
                    } else if (direction == JoyStick.STICK_DOWNRIGHT) {
                        textView5.setText("Direction : Down Right");
                        command = MainActivity.BACKWORD_RIGHT;
                    } else if (direction == JoyStick.STICK_DOWN) {
                        textView5.setText("Direction : Down");
                        command = MainActivity.BACKWORD;
                    } else if (direction == JoyStick.STICK_DOWNLEFT) {
                        textView5.setText("Direction : Down Left");
                        command = MainActivity.BACKWORD_LEFT;
                    } else if (direction == JoyStick.STICK_LEFT) {
                        textView5.setText("Direction : Left");
                        command = MainActivity.LEFT;
                    } else if (direction == JoyStick.STICK_UPLEFT) {
                        textView5.setText("Direction : Up Left");
                        command = MainActivity.FORWARD_LEFT;
                    } else if (direction == JoyStick.STICK_NONE) {
                        textView5.setText("Direction : Center");
                    }
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView1.setText("X :");
                    textView2.setText("Y :");
                    textView3.setText("Angle :");
                    textView4.setText("Distance :");
                    textView5.setText("Direction :");
                }

                ((MainActivity) getActivity()).sendData(command);

                return true;
            }
        });

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ((MainActivity) getActivity()).sendData(MainActivity.RESET);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });

        return v;

    }

}
