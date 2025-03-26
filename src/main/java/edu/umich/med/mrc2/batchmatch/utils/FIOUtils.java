/*******************************************************************************
 *
 * (C) Copyright 2018-2020 MRC2 (http://mrc2.umich.edu).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Alexander Raskind (araskind@med.umich.edu)
 *
 ******************************************************************************/

package edu.umich.med.mrc2.batchmatch.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

public class FIOUtils {

	public static File changeExtension(File f, String newExtension) {

		if (FilenameUtils.getExtension(f.getPath()).equals(newExtension))
			return f;

		String newFileName = FilenameUtils.getFullPath(f.getAbsolutePath()) + FilenameUtils.getBaseName(f.getName())
				+ "." + newExtension;

		return new File(newFileName);
	}

	public static String calculateFileChecksum(String filePath) {

		try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
			return DigestUtils.md5Hex(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String calculateFileChecksum(File file) {

		try (InputStream is = Files.newInputStream(Paths.get(file.getAbsolutePath()))) {
			return DigestUtils.md5Hex(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static File getFileForLocation(String location) {

		if (location == null || location.trim().isEmpty())
			return null;

		Path filePath = null;
		try {
			filePath = Paths.get(location);
		} catch (Exception e) {
			System.out.println("File at " + location + " was not found.");
		}
		if (filePath != null) {

			File fileToReturn = filePath.toFile();
			if (fileToReturn.exists())
				return fileToReturn;
		}
		return null;
	}

	public static List<Path> findFilesByExtension(Path path, String fileExtension) {

		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path must be a directory!");
		}
		String ext = fileExtension.toLowerCase();
		List<Path> result = null;
		try (Stream<Path> walk = Files.walk(path)) {
			result = walk.filter(p -> !Files.isDirectory(p)).filter(p -> p.toString().toLowerCase().endsWith(ext))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<Path> findDirectoriesByName(Path path, String dirName) {

		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path must be a directory!");
		}
		List<Path> result = null;
		try (Stream<Path> walk = Files.walk(path)) {
			result = walk.filter(p -> Files.isDirectory(p)).filter(p -> p.getFileName().toString().equals(dirName))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<Path> findDirectoriesByExtension(Path path, String fileExtension) {

		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path must be a directory!");
		}
		String ext = fileExtension.toLowerCase();
		List<Path> result = null;
		try (Stream<Path> walk = Files.walk(path)) {
			result = walk.filter(p -> Files.isDirectory(p)).filter(p -> p.toString().toLowerCase().endsWith(ext))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<Path> findFilesByName(Path path, String fileName) {

		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path must be a directory!");
		}
		List<Path> result = null;
		try (Stream<Path> walk = Files.walk(path)) {
			result = walk.filter(p -> !Files.isDirectory(p))
					.filter(p -> p.getName(p.getNameCount() - 1).toString().toLowerCase().equalsIgnoreCase(fileName))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<Path> findFilesByNameStartingWith(Path path, String nameStart) {

		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path must be a directory!");
		}
		String nameStartSearch = nameStart.toLowerCase();
		List<Path> result = null;
		try (Stream<Path> walk = Files.walk(path)) {
			result = walk.filter(p -> !Files.isDirectory(p))
					.filter(p -> p.getName(p.getNameCount() - 1).toString().toLowerCase().startsWith(nameStartSearch))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<Path> findDirectoriesByNameStartingWith(Path path, String nameStart) {

		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path must be a directory!");
		}
		String nameStartSearch = nameStart.toLowerCase();
		List<Path> result = null;
		try (Stream<Path> walk = Files.walk(path)) {
			result = walk.filter(p -> Files.isDirectory(p))
					.filter(p -> p.getName(p.getNameCount() - 1).toString().toLowerCase().startsWith(nameStartSearch))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void findFilesByNameRecursively(File sourceDir, String fileName, Set<File> results) {

		if (!sourceDir.isDirectory())
			throw new IllegalArgumentException("Source must be a directory!");

		if (sourceDir.canRead()) {

			for (File temp : sourceDir.listFiles()) {
				if (temp.isDirectory()) {
					findFilesByNameRecursively(temp, fileName, results);
				} else {
					if (fileName.equals(temp.getName())) {
						results.add(temp);
					}
				}
			}
		} else {
			throw new IllegalArgumentException(sourceDir.getAbsoluteFile() + "Permission Denied");
		}
	}
	
	public static void replaceFile(File newFile, File oldFile) throws IOException {
		
	    Files.move(newFile.toPath(), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static void replaceFile(Path newFile, Path oldFile) throws IOException {
		
	    Files.move(newFile, oldFile, StandardCopyOption.REPLACE_EXISTING);
	}
}
