package com.ivanferdelja.harald;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.agentcash.harald.R;
import com.spire.posmate.Logger;
import com.spire.posmate.PosmateDevice;
import com.spire.posmate.packet.Packet;
import com.spire.posmate.protocol.PaymentProcessor;


public class MainActivity extends Activity {

    private static final String TAG = "harald";

    private TextView messageTextView;
    private Button connectButton;
    private Button cancelButton;

    private PosmateDevice posmateDevice;
    private Thread posmateThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView2 = (TextView) findViewById(R.id.text);
        textView2.setText(Html.fromHtml(getString(R.string.hello_world)), TextView.BufferType.SPANNABLE);

        messageTextView = (TextView) findViewById(R.id.message);
        connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runProcessPayment();
            }
        });
        cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runCancelPayment();
            }
        });
    }

    private void runCancelPayment() {
        messageTextView.append("\n\nTo PosMate: ENQ sent.");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                posmateDevice.sendBytes(new byte[]{Packet.ENQ});
                //posmateThread.interrupt();
            }
        });
        t.start();
    }

    private Logger createLogger() {
        messageTextView.setText("");
        return new Logger() {
            @Override
            public void leaveSomeSpace() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageTextView.append("\n");
                    }
                });
            }

            @Override
            public void log(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageTextView.append("\n");
                        messageTextView.append(msg);
                    }
                });
            }
        };
    }

    private void runProcessPayment() {
        final Logger logger = createLogger();
        posmateThread = new Thread(new Runnable() {
            @Override
            public void run() {
//            Looper.prepare();
//            Handler handler = new Handler() {
//                @Override
//                public void handleMessage(android.os.Message msg) {
//                    super.handleMessage(msg);


                        posmateDevice = PosmateDevice.create();
                        if (posmateDevice == null) {
                            logger.log("Failed to create Posmate device.");
                            return;
                        }

                        logger.log("Connecting to Posmate... ");
                        if (posmateDevice.connect()) {
                            logger.log("Posmate did connect!");


                            PaymentProcessor processor = new PaymentProcessor(posmateDevice, logger);
                            processor.processPayment();

                            //posmateDevice.disconnect();
                            logger.leaveSomeSpace();
                            logger.log(posmateDevice.isConnected() ? "PosMate connected" : "PosMate disconnected");
                        } else {
                            logger.log("Posmate failed to connect!");
                        }
//                }
            };
//            handler.sendEmptyMessage(0);
//            Looper.loop();
//            }
        });
        posmateThread.start();
    }

//    EPOS_Initialization_12
//    [STX] 2
//            [ID] 12
//            [SEQ] 1
//            [SERIAL] 36408082
//            [TERMINAL_TYPE] 22
//            [TERM_COUNTRY_CODE] 191
//            [CAPABILITIES] E0F8C8
//    [ADDITIONAL] E000F0A001
//    [TERM_CURRENCY_CODE] 191
//            [TERM_CURRENCY_EXPO] 2
//            [MERCHANT_CATEGORY] 7299
//            [MERCHANT_NAME] Live Merchant
//    [FS] 28
//            [TRANS_CATEGORY_CODE] R
//    [FS] 28
//            [TIMEOUT1] -1
//            [FS] 28
//            [TIMEOUT2] -1
//            [FS] 28
//            [TIMEOUT3] -1
//            [FS] 28
//            [OPERATOR_PIN] -1
//            [FS] 28
//            [MERCHANT_LANG] hr
//    [ETX] 3

//    EPOS_StartProcess_41
//    [STX] 2
//            [ID] 41
//            [SEQ] 1
//            [41000] 2
//            [41001] 32
//            [ETX] 3
//
}
