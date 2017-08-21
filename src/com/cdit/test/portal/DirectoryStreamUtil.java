package com.cdit.test.portal;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.nio.file.Files;

public class DirectoryStreamUtil {
	public static void main(String[] asd){
//		showFile("C:/xmldata/backup/20161202");
		showFileFilters("C:/xmldata/backup/20161202",".xml");
//		showDirFilters("C:/xmldata");
	}
	public static void showFile(String dir){
		Path baseDir = Paths.get(dir);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(baseDir)) {
		    for (Path file: stream) {
		        System.out.println(file.getFileName());
		    }
		} catch (IOException | DirectoryIteratorException x) {
		    // IOException can never be thrown by the iteration.
		    // In this snippet, it can only be thrown by newDirectoryStream.
		    System.err.println(x);
		}
	}
	public static void showFileFilters(String dir, String ext){
		Path baseDir = Paths.get(dir);
		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>()
		{
		    public boolean accept(Path entry)
		    {
		        return entry.getFileName().toString().toLowerCase().endsWith(".xml");
//		            && Files.isRegularFile(entry);
		    }
		};
		try (
				final DirectoryStream<Path> stream = Files.newDirectoryStream(baseDir, filter);
		) {
			for(Path path:stream){
				System.out.println(path.getFileName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void showDirFilters(String dir){
		Path baseDir = Paths.get(dir);
		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
			    public boolean accept(Path file) throws IOException {
			        try {
			            return (Files.isDirectory(file));
			        } catch (Exception x) {
			            // Failed to determine if it's a directory.
			            System.err.println(x);
			            return false;
			        }
			    }
			};
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(baseDir, filter)) {
				for (Path entry: stream) {
					System.out.println(entry.getFileName());
				}
			} catch (IOException x) {
				System.err.println(x);
			}
	}
	
}
