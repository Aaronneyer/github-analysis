package com.matrobot.gha.dataset;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


/**
    Sample data:
        {
        "created_at": "2012-04-01T00:00:00Z",
        "payload": {
            "ref_type": "repository",
            "ref": null,
            "description": "a project with ROR",
            "master_branch": "master"
        },
        "repo": {
            "id": 3889255,
            "url": "https://api.github.dev/repos/azonwan/rable",
            "name": "azonwan/rable"
        },
        "type": "CreateEvent",
        "public": true,
        "actor": {
            "avatar_url": "https://secure.gravatar.com/avatar/ca087d20dcf8cb24426c3e816adcdda7?d=http://github.dev%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
            "gravatar_id": "ca087d20dcf8cb24426c3e816adcdda7",
            "id": 830226,
            "url": "https://api.github.dev/users/azonwan",
            "login": "azonwan"
        },
        "id": "1536460828"
    }
 */
public class FileDatasetReader implements IDatasetReader{

	private String fileContents;
	private Gson gson = new Gson();
	private String filename;
	
	
	/**
	 * Init reader from file path
	 * @param filePath
	 * @throws IOException 
	 */
	public FileDatasetReader(String filePath) throws IOException{
	
		filename = filePath;
		initContent(new FileInputStream(filePath));
	}
	
	
	/**
	 * Init reader from stream
	 * @param inputStream
	 * @throws IOException 
	 */
	public FileDatasetReader(InputStream inputStream) throws IOException{

		initContent(inputStream);
	}


	private void initContent(InputStream inputStream) throws IOException,
			UnsupportedEncodingException {
		InputStream gzipStream = new GZIPInputStream(inputStream);
		Reader reader = new InputStreamReader(gzipStream, "UTF-8");
		char[] buffer = new char[100000];
		int count;
		
		StringBuilder contents = new StringBuilder();
		while((count=reader.read(buffer)) > 0){
			contents.append(buffer, 0, count);
		}
		fileContents = contents.toString();
		if(fileContents.indexOf("\n") == -1){
			fileContents = fileContents.replace("}{", "}\n{");
		}
	}


	public DataRecord readNextRecord(){
		
		DataRecord data = null;
		
		try {
			String line = readLine();
			if(line != null){
				try{
					data = gson.fromJson(line, DataRecord.class);
				}catch(JsonSyntaxException e){
					System.err.println(e);
					System.err.println(line);
					System.err.println(filename);
					System.err.println();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return data;
	}


	private String readLine() throws IOException {
		
		String line = null;
		
		if(fileContents.length() > 0){
			int index = fileContents.indexOf("\n");
			if(index > 0){
				line = fileContents.substring(0, index);
				fileContents = fileContents.substring(index).trim();
			}
			else{
				line = fileContents;
				fileContents = "";
			}
		}
		
		return line;
	}
	
}

