package com.cdit.test.portal;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 
 * @author anirudh
 *
 */
public class FileDescriptorExam{

	public static void main(String[] args) throws IOException {

		FileInputStream fileInputStream = new FileInputStream(new File(
				"c:\\xmldata\\222.xml"));
		FileDescriptor fd = fileInputStream.getFD();
		System.out.println(" File descriptor of the file c:\\xmldata\\222.xml : "
						+ fd.hashCode());

		

		FileOutputStream fileOutputStream = new FileOutputStream(new File(
				"c:\\xmldata\\222.xml"));
		FileDescriptor fd2 = fileOutputStream.getFD();
		System.out.println(" File descriptor of the file c:\\xmldata\\222.xml : "
						+ fd2.hashCode());

		fileOutputStream.close();
		
		//Making another FileInput Stream object with file descriptor
		FileInputStream anotFileInputStream = new FileInputStream(fd);
		
		//check value of file descriptor
		System.out.println("Value of File Desciptor : "+anotFileInputStream.getFD().hashCode());
		
		//See the value of file
		int i=0;
	    while((i=anotFileInputStream.read())!=-1){
	    	System.out.print((char)i);
	    }
	    
	    fileInputStream.close();
	    anotFileInputStream.close();

	}

}