package com.example.user.androidproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class MailService extends Service {
    private String user = ""; // 진짜 아이디 적으면 됨
    private String password = ""; // 진짜 패스워드 적으면 됨
    private String email;

    public MailService() {}

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        email = intent.getStringExtra("email");
        try {
            GMailSender gMailSender = new GMailSender(user, password);
            gMailSender.sendMail("비밀번호 변경 관련 안내 드릴게요", "임시 패스워드는 1234입니다.", email);
            //Toast.makeText(context, "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
        } catch (SendFailedException e) {
            //Toast.makeText(context, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            System.out.println(e);
            //Toast.makeText(context, "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}