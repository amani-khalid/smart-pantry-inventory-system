package repo;

import models.PantryItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * TXT-based implementation of PantryRepository. Uses pipe-delimited format for
 * better readability.
 */
public class FilePantryRepository implements PantryRepository {

	private final Path filePath;
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

	public FilePantryRepository(String filePath) {
		this.filePath = Paths.get(filePath);
		ensureDirectoryExists(this.filePath.getParent());
		ensureFileExists(this.filePath);
	}

	private void debugFileContent() {
		try {
			Path filePath = Paths.get("pantryitems.txt");
			System.out.println("=== FILE CONTENT ===");
			System.out.println("File exists: " + Files.exists(filePath));
			System.out.println("File size: " + Files.size(filePath) + " bytes");

			List<String> allLines = Files.readAllLines(filePath);
			for (int i = 0; i < allLines.size(); i++) {
				System.out.println("Line " + i + ": " + allLines.get(i));
			}
			System.out.println("=== END FILE CONTENT ===");
		} catch (Exception e) {
			System.out.println("Error reading file: " + e.getMessage());
		}
	}

	@Override
	public List<PantryItem> loadAll(String userId) {
		// IGNORE userId - load all items from shared pantry
		List<PantryItem> list = new ArrayList<>();

		if (!Files.exists(filePath)) {
			System.out.println("Shared pantry file not found: " + filePath);
			return list;
		}

		try (BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
			String line;
			int itemCount = 0;

			while ((line = br.readLine()) != null) {
				line = line.trim();

				// Skip empty lines, comments, and user sections (if any remain)
				if (line.isEmpty() || line.startsWith("#") || line.startsWith("UserID:")) {
					continue;
				}

				// Process all item lines
				PantryItem item = parse(line);
				if (item != null) {
					list.add(item);
					itemCount++;
					System.out.println("Loaded shared item: " + item.getName() + " | Qty: " + item.getQuantity());
				}
			}

			System.out.println("Total shared pantry items loaded: " + itemCount);

		} catch (IOException e) {
			System.err.println("Error loading shared pantry items: " + e.getMessage());
		}

		return list;
	}

	@Override
	public void saveAll(String userId, List<PantryItem> items) {
		// IGNORE userId - save all items to shared pantry
		try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {

			// Write shared pantry header
			bw.write("# Shared Family Pantry - All users see the same items");
			bw.newLine();
			bw.write("# Format: ID|Name|Category|Quantity|Unit|Threshold|ExpiryDate");
			bw.newLine();
			bw.newLine();

			// Write all items (no user separation)
			for (PantryItem item : items) {
				String itemLine = format(item);
				bw.write(itemLine);
				bw.newLine();
			}

			System.out.println("Saved " + items.size() + " items to shared pantry");

		} catch (IOException e) {
			System.err.println("Error saving shared pantry items: " + e.getMessage());
		}
	}

	private PantryItem parse(String line) {
		try {
			System.out.println("Parsing line: " + line);

			String[] parts = line.split("\\|");
			if (parts.length != 7) {
				System.err.println("Invalid item format - expected 7 parts, got " + parts.length + ": " + line);
				return null;
			}

			String id = parts[0].trim();
			String name = parts[1].trim();
			String category = parts[2].trim();
			double quantity = Double.parseDouble(parts[3].trim());
			String unit = parts[4].trim();
			double threshold = Double.parseDouble(parts[5].trim());

			LocalDate expiryDate = null;
			String expiryStr = parts[6].trim();
			if (!expiryStr.isEmpty() && !expiryStr.equals("null") && !expiryStr.equals("NULL")) {
				expiryDate = LocalDate.parse(expiryStr, DATE_FMT);
			}

			PantryItem item = new PantryItem(id, name, category, quantity, unit, threshold, expiryDate);
			System.out.println("Successfully parsed: " + item.getName());
			return item;

		} catch (Exception e) {
			System.err.println("Error parsing item line: " + line + " - " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private String format(PantryItem item) {
		String dateStr = "";
		if (item.getExpiryDate() != null) {
			dateStr = item.getExpiryDate().format(DATE_FMT);
		}

		return String.join("|", item.getitemID() != null ? item.getitemID() : "ITEM_" + System.currentTimeMillis(),
				item.getName() != null ? item.getName() : "Unknown",
				item.getCategory() != null ? item.getCategory() : "Other", String.valueOf(item.getQuantity()),
				item.getUnit() != null ? item.getUnit() : "pcs", String.valueOf(item.getThreshold()), dateStr);
	}

	private void ensureDirectoryExists(Path dir) {
		if (dir == null)
			return;
		try {
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
				System.out.println("Created directory: " + dir);
			}
		} catch (IOException e) {
			System.err.println("Error creating directory: " + e.getMessage());
		}
	}

	private void ensureFileExists(Path file) {
		if (Files.exists(file)) {
			return;
		}
		try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
			// Write initial header
			bw.write("# Smart Pantry Items Database");
			bw.newLine();
			bw.write("# Each user section starts with 'UserID: <userId>'");
			bw.newLine();
			bw.write("# Format: ID|Name|Category|Quantity|Unit|Threshold|ExpiryDate");
			bw.newLine();
			System.out.println("Created new pantry file: " + file);
		} catch (IOException e) {
			System.err.println("Error creating file: " + e.getMessage());
		}
	}
}