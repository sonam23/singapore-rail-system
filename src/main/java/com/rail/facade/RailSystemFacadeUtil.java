package com.rail.facade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

/**
 * Util and helper class for the facade implementation.
 * @author sagarwal
 */
@Component("railSystemFacadeUtil")
public class RailSystemFacadeUtil {
	
	/**
	 * Assumption: 1. The first line is always a header and is ignored
	 * 2. the code, name, date is comma separated and in separate lines
	 * @return
	 */
	public ArrayList<String[]> readFile(String filePath) {
		ArrayList<String[]> stations = new ArrayList<>();
		List<String> lines = readLinesUsingFiles(filePath);
		if(lines != null ) {
			lines.forEach(line -> {
				System.out.println(line);
				String[] words = line.split(",");
				stations.add(words);
			});
			return stations;
		}
		return null;
	}
	
	/**
	 * Reads the StationMap.csv file
	 * Skips the first line of the file, as this would be the header of the CSV
	 * @param fileName
	 * @return
	 */
	private List<String> readLinesUsingFiles(String fileName) {
		try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
		    return lines.skip(1).collect(Collectors.toList());
		} catch (IOException io) {
			io.printStackTrace();
		}
		return null;
	}

}
