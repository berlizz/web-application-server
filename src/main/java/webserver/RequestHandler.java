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
import util.IOUtils;

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
            
            if(body != null) {
            	response200Header(dos, body.length);
            } else {
            	response302Header(dos);
            }
            
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private byte[] readHeader(BufferedReader reader, byte[] body) {
    	try {
			String line = reader.readLine();
			String[] lineArr = new String[line.length()];
			
			if(line != null) {
				lineArr = line.split(" ");
			}
			
			if(lineArr[0].equals("POST")) {
				body = readPOSTHeader(reader, body, lineArr);
				
				return body;
			}
			
			body = readGETHeader(reader, body, lineArr);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
    	
    	return body;
    	
    }
    
    private byte[] readGETHeader(BufferedReader reader, byte[] body, String[] lineArr) {
    	try {
			String line = reader.readLine();
			
			while(!line.equals("")) {
				if(!lineArr[1].equals("/")) {
					if(lineArr[1].contains("/user/create?")) {
						lineArr[1] = signUpGET(lineArr[1]);
					}
					String filePath = "./webapp";
			    	body = Files.readAllBytes(Paths.get(filePath + lineArr[1]));
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
    
    private byte[] readPOSTHeader(BufferedReader reader, byte[] body, String[] lineArr) {
    	try {
    		String line = reader.readLine();
    		int contentLength = 0;
    		
    		while(!line.equals("")) {
    			if(line.contains("Content-Length")) {
    				int index = line.indexOf(":");
    				contentLength = Integer.parseInt(line.substring(index + 2));
    			}
    			
    			line = reader.readLine();
    			
    			if(line == null) {
    				break;
    			}
    		}
    		
    		String userData = IOUtils.readData(reader, contentLength);
    		signUpPOST(userData);
    		
    		//body = Files.readAllBytes(Paths.get("./webapp/index.html"));
    		body = null;
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
    	
    	return body;
    }
    
    private void signUpPOST(String userData) {
    	Map<String, String> map = HttpRequestUtils.parseQueryString(userData);
    	User newUser = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));

    	log.info("signUpPOST - {}", newUser.toString());
    }
    
    private String signUpGET(String signUpUrl) {
    	int index = signUpUrl.indexOf("?");
    	String path = "/index.html";
    	String userData = signUpUrl.substring(index + 1);
    	
    	Map<String, String> map = HttpRequestUtils.parseQueryString(userData);
    	User newUser = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));

    	log.info("signUpGET - {}", newUser.toString());
    	
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
    
    private void response302Header(DataOutputStream dos) {
    	try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: http://localhost:8080/index.html\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
        	if(body != null) {
        		dos.write(body, 0, body.length);
        	}
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
