package com.watchDog.project.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@Service
public class TCPHealthCheckService {

    @Value("${health.check.prot}")
    private int port; // 헬스 체크를 수신할 포트 번호

    @Value("${health.check.reqmsg.yn}")
    private String reqMsgYn;    // 헬스 체크 메세지 로그 남길지 여부

    private ServerSocket serverSocket;



    public void startServer() {
        try {
            serverSocket = new ServerSocket(port) ;
            log.info("TCP 헬스 체크 서버가 시작되었습니다. 포트: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("############# TCP 헬스 체크 요청이 수신되었습니다. #############");

                // 클라이언트로부터 데이터를 받아오는 부분
                InputStream inputStream = clientSocket.getInputStream();
                byte[] buffer = new byte[1024];

                if("Y".equals(reqMsgYn)){
                    int bytesRead = inputStream.read(buffer);
                    String requestData = new String(buffer, 0, bytesRead);
                    log.info("수신된 데이터: " + requestData);
                }

                // 헬스 체크 응답 전송
                String response = "OK"; // 성공 상태 응답 (변경 가능)
                clientSocket.getOutputStream().write(response.getBytes());

                // 클라이언트 소켓 종료
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                log.info("############# TCP 헬스 체크 서버가 종료되었습니다. ############# 포트: " + port);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
