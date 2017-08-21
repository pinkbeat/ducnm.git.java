package com.cdit.test.portal;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Set;
/*
 * https://jakubstas.com/file-attributes-nio-2/#.WMDEgTuGPIU
 */
public class FileAttributeUtil {
	static Path path;
	public static void main(String[] asd) throws IOException{
		path = new File(asd[0]).toPath();
//		showCurrentStateOfTheFile("before any modification");
//		createUserDefinedAttribute("ducnm","no1");
//		showCurrentStateOfTheFile("after adding user defined attributes");
//		createUserDefinedAttribute("addr","BS");
//		showCurrentStateOfTheFile("after modifying user defined attributes");
//		deleteUserDefinedAttributes();
//		showCurrentStateOfTheFile("after removing user defined attributes");				
		
		final Path path = Paths.get("/usr/java/jdk1.7.0_25/src.zip");
		final Boolean isDirectoryAttr = (Boolean) Files.getAttribute(path, "isDirectory", LinkOption.NOFOLLOW_LINKS);
		final Long sizeAttr = (Long) Files.getAttribute(path, "basic:size", LinkOption.NOFOLLOW_LINKS);
		final UserPrincipal ownerAttr = (UserPrincipal) Files.getAttribute(path, "posix:owner", LinkOption.NOFOLLOW_LINKS);

		System.out.println("Attribute 'isDirectory': " + isDirectoryAttr);
		System.out.println("Attribute 'size': " + sizeAttr);
		System.out.println("Attribute 'owner': " + ownerAttr.getName());
		
		
		final UserPrincipalLookupService userPrincipalLookupService = FileSystems.getDefault().getUserPrincipalLookupService();
		final UserPrincipal me = userPrincipalLookupService.lookupPrincipalByName("jstas");
		final UserPrincipal root = userPrincipalLookupService.lookupPrincipalByName("root");

		final UserPrincipal ownerOld = (UserPrincipal) Files.getAttribute(path, "posix:owner");

		if (ownerOld.equals(me)) {
		    Files.setAttribute(path, "posix:owner", root);
		} else {
		    Files.setAttribute(path, "posix:owner", me);
		}

		final UserPrincipal ownerNew = (UserPrincipal) Files.getAttribute(path, "posix:owner");

		System.out.println("Attribute 'owner' before: " + ownerOld);
		System.out.println("Attribute 'owner' after: " + ownerNew);
	}
	private static void getZipFileAttribute() throws IOException{
		final FileSystem defaultFS = FileSystems.getDefault();
		final FileSystem zipFs = getZipFS();

		for (String fileAttributeView : defaultFS.supportedFileAttributeViews()) {
		    System.out.println("Default file system supports: " + fileAttributeView);
		}

		System.out.println();

		for (String fileAttributeView : zipFs.supportedFileAttributeViews()) {
		    System.out.println("ZIP file system supports: " + fileAttributeView);
		}
		
		for (FileStore fs : FileSystems.getDefault().getFileStores()) {
		    // returns true
		    fs.supportsFileAttributeView(BasicFileAttributeView.class);
		}
		Path zipPath = new File("c:\\a.zip").toPath();
		try (FileSystem zipFS = FileSystems.newFileSystem(zipPath, null)) {
		    for (FileStore fs : zipFs.getFileStores()) {
		        // returns true
		        fs.supportsFileAttributeView(BasicFileAttributeView.class);
		        // returns false
		        fs.supportsFileAttributeView(DosFileAttributeView.class);
		        // returns false
		        fs.supportsFileAttributeView("acl");
		    }
		}
	}
	private static FileSystem getZipFS() {
		// TODO Auto-generated method stub
		return null;
	}
	private static void getPosixFileAttribute() throws IOException{
		final Path path = Paths.get("/usr/java/jdk1.7.0_25/src.zip");
		// get permissions from posix view
		final PosixFileAttributeView posixView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
		final PosixFileAttributes attrs = posixView.readAttributes();
		final Set<PosixFilePermission> permessions = attrs.permissions();

		// display all the permissions
		System.out.println("\nPermissions before modification:");

		for (PosixFilePermission permission : permessions) {
		    System.out.println(permission);
		}

		// clear all the permissions
		permessions.clear();

		// set own permissions
		permessions.add(PosixFilePermission.OWNER_READ);
		permessions.add(PosixFilePermission.OWNER_WRITE);
		permessions.add(PosixFilePermission.OWNER_EXECUTE);

		// modife file attributes
		posixView.setPermissions(permessions);

		System.out.println("\nPermissions after modification:");

		// verify results
		final Set<PosixFilePermission> permessionsNew = posixView.readAttributes().permissions();

		for (PosixFilePermission permission : permessionsNew) {
		    System.out.println(permission);
		}
	}
	private static void showCurrentStateOfTheFile(String suffix) throws IOException {
	    final UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
	    System.out.println("\nList of all user defined attributes " + suffix + ":\n");

	    for (String attribute : view.list()) {
	        System.out.println(attribute + ": " + getUserDefinedAttribute(view, attribute));
	    }
	    System.out.println("\nFile size: " + Files.getAttribute(path, "size") + " B\n\n");
	}
	private static void createUserDefinedAttribute(String name, String value) throws IOException {
	    final UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
	    if (!view.list().contains(name)) {
	        view.write(name, StandardCharsets.UTF_8.encode(value));
	    }
	}
	private static String getUserDefinedAttribute(UserDefinedFileAttributeView view, String attributeName) throws IOException {
	    if (view.list().contains(attributeName)) {
	        ByteBuffer buffer = ByteBuffer.allocateDirect(view.size(attributeName));
	        view.read(attributeName, buffer);
	        buffer.flip();
	        return StandardCharsets.UTF_8.decode(buffer).toString();
	    } else {
	        return "";
	    }
	}
	private static void deleteUserDefinedAttributes() throws IOException {
	    final UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
	    for (String attributeName : view.list()) {
	        view.delete(attributeName);
	    }
	}
}
