package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); 
        		OutputStream out = connection.getOutputStream();
        		BufferedReader reader = new BufferedReader(new InputStreamReader(in)) ) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = "Hello World".getBytes();
            
            body = readHeader(reader, body);
            
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private byte[] readHeader(BufferedReader reader, byte[] body) {
    	try {
			String line = reader.readLine();
			String[] strArr = new String[line.length()];
			if(line != null) {
				strArr = line.split(" ");
			}
			
			while(!line.equals("")) {
				if(!strArr[1].equals("/")) {
					if(strArr[1].contains("/user/create")) {
						strArr[1] = signUp(strArr[1]);
					}
					String filePath = "./webapp";
			    	body = Files.readAllBytes(Paths.get(filePath + strArr[1]));
				}
				line = reader.readLine();
				
				if(line == null) {
					break;
				}
			}
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
    	
    	return body;
    	
    }
    
    private String signUp(String signUpUrl) {
    	int index = signUpUrl.indexOf("?");
    	String path = signUpUrl.substring(0, index);
    	String userData = signUpUrl.substring(index + 1);
    	
    	Map<String, String> map = HttpRequestUtils.parseQueryString(userData);
    	User newUser = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));

    	log.info(newUser.toString());
    	
    	return path;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
