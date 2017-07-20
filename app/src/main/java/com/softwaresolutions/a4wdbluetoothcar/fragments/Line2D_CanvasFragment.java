
package com.softwaresolutions.a4wdbluetoothcar.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.softwaresolutions.a4wdbluetoothcar.R;
import com.softwaresolutions.a4wdbluetoothcar.activities.MainActivity;
import com.softwaresolutions.a4wdbluetoothcar.generated.Line2D;

import static com.softwaresolutions.a4wdbluetoothcar.activities.MainActivity.outputStream;
import static com.softwaresolutions.a4wdbluetoothcar.activities.MainActivity.pathView;

/**
 * Created by mahmoud on 19/07/17.
 */

public class Line2D_CanvasFragment extends Fragment {

    MainActivity mainActivity = new MainActivity();
    private LinearLayout layout_line2D;
    private Line2D line2D;
    private Button sendTrack_Btn;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.line2d_canvas_fragment, container, false);

        layout_line2D = (LinearLayout) v.findViewById(R.id.layout_line2d);
        sendTrack_Btn = (Button) v.findViewById(R.id.send_track_btn);

        line2D = new Line2D() {
            @Override
            public double getX1() {

                return 0;
            }

            @Override
            public double getY1() {
                return 0;
            }

            @Override
            public double getX2() {
                return 0;
            }

            @Override
            public double getY2() {
                return 0;
            }

            @Override
            public Point getP1() {
                return null;
            }

            @Override
            public Point getP2() {
                return null;
            }

            @Override
            public void setLine(double x1, double y1, double x2, double y2) {

            }
        };

        sendTrack_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (outputStream != null) {
                    if (pathView.stringList == null) {
                        Toast.makeText(mainActivity.getApplicationContext(), "No Path Drawn",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (pathView.stringList.size() > 0) {
                            mainActivity.stringList = pathView.stringList;
                            for (String s : mainActivity.stringList) {
                                mainActivity.sendData(s);
                            }
                            Toast.makeText(mainActivity.getApplicationContext(), "Message Sent",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mainActivity.getApplicationContext(), "Please draw a line.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    pathView.resetObstacleDetected();


                } else {
                    Toast.makeText(mainActivity.getApplicationContext(), "No Connection Found",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


        return v;
    }

}
