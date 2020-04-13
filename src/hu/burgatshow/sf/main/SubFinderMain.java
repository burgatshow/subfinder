package hu.burgatshow.sf.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;

public class SubFinderMain implements Serializable {
	private static final long serialVersionUID = 5929438987038607629L;

	private static final Set<String> allowedCLICommands = new TreeSet<String>(
			Arrays.asList("-F", "--folder", "-M", "--mapping", "-E", "--video-sizelimit", "-S", "--subtitle-sizelimit",
					"-V", "--verbose", "-T", "--test", "-H", "--help"));
	private static Map<String, String> parsedArgs = new HashMap<String, String>();

	private static Set<SeriesInfo> series = new TreeSet<SeriesInfo>();
	private static Map<Object, String> downloadables = new HashMap<Object, String>();
	private static Map<String, String> seriesNameIdMap = new TreeMap<String, String>();

	private static long videoSizeLimit = 300L;
	private static long subtitleSizeLimit = 5L;
	private static boolean verboseMode = false;
	private static boolean testMode = false;

	static {
		disableSslVerification();
	}

	public static void main(String[] args) throws IOException {

		// Dump header
		dumpHeaderAndFooter(true);

		// Parse arguments
		parseArguments(args);

		// Verbose mode enabled
		if (parsedArgs.containsKey("verbose")) {
			verboseMode = true;
			System.out.println("Verbose mode enabled by argument -V or --verbose.");

			System.out.println("Parsed arguments:");
			for (String a : parsedArgs.keySet()) {
				System.out.println("\t" + a + " = " + parsedArgs.get(a));
			}
		}

		// Test mode enabled
		if (parsedArgs.containsKey("test")) {
			testMode = true;
			System.out.println("Test mode enabled by argument -T or --test.");
		}

		// Display help if required
		if (parsedArgs.containsKey("help")) {
			printVerbose("Help requested, dumping help and stopping...");
			dumpHelp();
			System.exit(0);
		}

		if (parsedArgs.containsKey("videosizelimit")) {
			try {
				videoSizeLimit = Long.parseLong(parsedArgs.get("videosizelimit"));
			} catch (NumberFormatException e) {
			}
		}

		if (parsedArgs.containsKey("subtitlesizelimit")) {
			try {
				subtitleSizeLimit = Long.parseLong(parsedArgs.get("subtitlesizelimit"));
			} catch (NumberFormatException e) {
			}
		}

		printVerbose(String.format("\tVideo file size limit: %d MB", videoSizeLimit));
		printVerbose(String.format("\tSubtitle file size limit: %d KB", subtitleSizeLimit));

		loadSeriesMapping();
		fetchFilesInFolder();
		prepareDownload();
		downloadFiles();

		dumpHeaderAndFooter(false);
		System.exit(0);
	}

	/**
	 * Writes the tool's header to the console
	 */
	private static void dumpHeaderAndFooter(boolean isHeader) {
		System.out.println("\n\n=================================================");
		if (isHeader) {
			System.out.println("Subtitle Finder v0.0.0.0.6 (Speedster Tongue)");
			System.out.println("Author: burgatshow");
			System.out.println(new SimpleDateFormat("'Start time': yyyy. MM. dd. HH:mm:ss").format(new Date()));
		} else {
			System.out.println("\n\nTool finished working, now go and Netflix and chill! Oh wait. :)\n\n");
		}
		System.out.println("=================================================\n\n");
	}

	/**
	 * This method will parse and validate the required command line arguments
	 * 
	 * @param args {@link String} array of args received from command line
	 */
	private static void parseArguments(String[] args) {
		if (args.length == 0) {
			System.out.println("Please provide the required arguments. Use -h or --help to view the options.");
			System.exit(-1);
		}

		for (int i = 0; i < args.length; i++) {
			String currentArg = null;

			switch (args[i].charAt(0)) {
			case '-':
				if (args[i].length() < 2) { // Invalid 1 character long something
					System.err.println("ERROR: Invalid argument: " + args[i]);
				} else {
					currentArg = args[i];
				}

				if (args[i].length() == 2 && !allowedCLICommands.contains(args[i])) {
					System.err.println("ERROR: Not allowed argument: " + args[i]);
				} else {
					switch (args[i]) {
					case "--folder":
					case "-F":
						currentArg = "--folder";
						break;

					case "--mapping":
					case "-M":
						currentArg = "--mapping";
						break;

					case "-E":
					case "--video-sizelimit":
						currentArg = "--video-sizelimit";
						break;

					case "-S":
					case "--subtitle-sizelimit":
						currentArg = "--subtitle-sizelimit";
						break;

					case "--verbose":
					case "-V":
						currentArg = "--verbose";
						break;

					case "--test":
					case "-T":
						currentArg = "--test";
						break;

					case "--help":
					case "-H":
						currentArg = "--help";
						break;

					default:
						throw new IllegalArgumentException("ERROR: Unexpected value: " + args[i]);
					}
				}

				String arg = currentArg.replaceAll("-", "");

				switch (arg) {
				case "folder":
				case "mapping":
				case "videosizelimit":
				case "subtitlesizelimit":
					parsedArgs.put(arg, args[i + 1]);
					break;
				case "verbose":
				case "test":
				case "help":
				default:
					parsedArgs.put(arg, "yes");
					break;
				}

				break;

			default:
				break;
			}

		}
	}

	/**
	 * Dumps the help menu of the tool
	 */
	private static void dumpHelp() {
		System.out.println(
				"\nThis tool is designed to download proper subtitle files from https://feliratok.info.\nThe tool is taking responsibility of the proper release, so if there is no subtitle \nfor the specific release, it will not download anything.\n\nIf it finds the proper subtitle, it will download it and place it next to the video\nfile with the same format like the original video file has.\n");

		System.out.println("Usage:\n\tjava -jar SubFinder.jar --folder path --mapping path [ -ESVTH ]\n");

		System.out.println("The program accepts the following arguments:\n\n");
		System.out.println(
				"\t-F, --folder\t\t\tThe folder where the tool can scan the series.\n\t\t\t\t\tObviously this is the folder where your files are downloaded.\n");
		System.out.println(
				"\t-M, --mapping\t\t\tThe path where from the tool can load the special properties\n\t\t\t\t\tfile which contains series=id mappings. Required because of the\n\t\t\t\t\tsite where we are downloading from.\n");
		System.out.println(
				"\t-E, --video-sizelimit\t\tThe minimum file size in MB to consider a valid video file.\n\t\t\t\t\tIf not set, 300 MB will be used.\n");
		System.out.println(
				"\t-S, --subtitle-sizelimit\tThe minimum file size in KB to consider a valid subtitle file.\n\t\t\t\t\tIf not set, 5 KB will be used.\n");
		System.out.println("\t-V, --verbose\t\t\tProvides verbose, detailed output when running.\n");
		System.out.println(
				"\t-T, --test\t\t\tTest run (or dry run) with the given configuration. It will\n\t\t\t\t\tsimulate what will happen, but it does not download and write\n\t\t\t\t\tanything to the disk.\n");
		System.out.println(
				"\t-H, --help\t\t\tDisplays this help and exits.Keep in mind, that until this\n\t\t\t\t\targument is present, the tool will not do anything!\n");
	}

	/**
	 * Loads the mapping file provided in the --mapping argument
	 * 
	 * @param mappingFileArg
	 */
	private static void loadSeriesMapping() throws IOException, IllegalArgumentException {
		System.out.println(
				String.format("\n#1 - Loading series mapping from configured file: %s", parsedArgs.get("mapping")));

		if (parsedArgs.get("mapping") == null || parsedArgs.get("mapping").isEmpty()) {
			throw new IllegalArgumentException(
					"ERROR: The provided mapping file path parameter is null or empty... Check it again!");
		} else {
			printVerbose("\t - The provided mapping file path seems OK.");
		}

		Path mappingFile = Paths.get(parsedArgs.get("mapping"));
		if (!mappingFile.toFile().exists() && !mappingFile.toFile().isHidden() && mappingFile.toFile().isFile()) {
			throw new IllegalArgumentException(
					"ERROR: The provided mapping file is not a file, does not exist or it is hidden!");
		} else {
			printVerbose("\t - The mapping file on path is an existing, not hidden file. Testing read permission...");
		}

		if (!Files.isReadable(mappingFile)) {
			throw new IllegalArgumentException(
					"ERROR: The provided mapping file is not readable by the tool. Check the file permissions!");
		} else {
			printVerbose("\t - The file is readable by the tool, let's read...");
		}

		InputStream input = new FileInputStream(mappingFile.toString());
		Properties prop = new Properties();
		prop.load(input);

		String k = null;
		for (Object key : prop.keySet()) {
			k = key.toString().replaceAll("_", " ").toUpperCase();
			seriesNameIdMap.put(k, prop.get(key).toString());
		}

		for (Map.Entry<String, String> entries : seriesNameIdMap.entrySet()) {
			printVerbose(String.format("\t\t - %s: %s", entries.getKey(), entries.getValue()));
		}

		printVerbose(String.format("\t - A total of %s series loaded from the file.", seriesNameIdMap.size()));

		System.out.println("#1 - Loading series mapping from configured file:  FINISHED!\n\n");
	}

	/**
	 * Examines the provided folde and collects the required data
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	private static void fetchFilesInFolder() throws IOException, IllegalArgumentException {
		System.out.println(
				String.format("#2 - Fetching content of the provided directory: %s", parsedArgs.get("folder")));

		if (parsedArgs.get("folder") == null || parsedArgs.get("folder").isEmpty()) {
			throw new IllegalArgumentException(
					"ERROR: The provided working directory path parameter is null or empty... Check it again!");
		} else {
			printVerbose("\t - The provided folder path seems OK.");
		}

		Path videoFolder = Paths.get(parsedArgs.get("folder"));
		if (!videoFolder.toFile().exists() && !videoFolder.toFile().isHidden() && videoFolder.toFile().isDirectory()) {
			throw new IllegalArgumentException(
					"ERROR: The provided content directory does not exist, it is hidden or not even a directory!");
		} else {
			printVerbose(
					"\t - The content folder path is an existing, not hidden directory. Testing read permission...");
		}

		if (!Files.isReadable(videoFolder)) {
			throw new IllegalArgumentException(
					"ERROR: The provided content directory is not readable by the tool. Check the file permissions!");
		} else {
			printVerbose("\t - The file is readable by the tool, let's read...");
		}

		Pattern pattern = Pattern.compile("(.*)S([0-9]{1,2})E([0-9]{1,2})(.*)(720|1080)(.*)-([a-zA-Z\\-]+)");

		Files.list(videoFolder).sorted().filter(Files::isDirectory).forEach(path -> {
			String name = path.getFileName().toString();

			if (name != null && !name.isEmpty()) {
				printVerbose(String.format("\t\t - %s", name));
				Matcher matcher = pattern.matcher(name);
				if (matcher.find()) {
					SeriesInfo si = new SeriesInfo(name);
					si.setTitle(matcher.group(1).replaceAll("\\.", " ").trim());

					Object seriesID = seriesNameIdMap.get(si.getUpperTitle());
					if (seriesID != null && !seriesID.toString().isEmpty()) {
						si.setId(seriesID.toString());
					}

					long videoFileSizeLimit = 300L;
					long subtitleFileSizeLimit = 5L;

					try {
						DirectoryStream<Path> files = Files
								.newDirectoryStream(Paths.get(videoFolder.toString(), si.getFoldername()));

						// Need to sort the files read from filesystem because DirectoryStream does not
						// sort it, so items can be read in any order --> may break the logic for
						// isSubDownloadRequired() if the last file will be the video
						List<Path> sortedFiles = new ArrayList<Path>();
						files.forEach(sortedFiles::add);
						sortedFiles.sort(Comparator.comparing(Path::toString));

						String[] f = null;
						long filesize = 0;
						for (Path p : sortedFiles) {
							f = p.toString().split("\\.(?=[^\\.]+$)");
							filesize = p.toFile().length();

							if (f[1].equalsIgnoreCase("mkv") && videoFileSizeLimit < filesize / 1024 / 1024) {
								System.out.println(String.format("\t\t\t - Found video file: %s, filesize (MB): %d",
										p.toString(), filesize / 1024 / 1024));
								si.setVideoFileName(p.toString());
								si.setSubFileName(f[0] + ".srt");
								si.setSubDownloadRequired(true);

							}

							if (f[1].equalsIgnoreCase("srt") && subtitleFileSizeLimit < filesize / 1024) {
								System.out.println(String.format("\t\t\t - Found subtitle file: %s, filesize (KB): %d",
										p.toString(), filesize / 1024));
								si.setSubFileName(p.toString());
								si.setSubDownloadRequired(false);
							}
						}
					} catch (IOException e) {
						throw new IllegalStateException(
								"Could not fetch provided folder and its content. Terminating...");
					}

					si.setSeason(matcher.group(2));
					si.setEpisode(matcher.group(3));
					si.setQuality(matcher.group(5));
					si.setReleaser(matcher.group(7));
					series.add(si);
				} else {
					System.out.println("\t\t\t - No series specific info found, probably movie.");
				}
			}

		});

		printVerbose(
				String.format("\t - A total of %s directories examined from in working directory.", series.size()));
		System.out.println(String.format("#2 - Fetching content of the provided directory: FINISHED!\n\n"));
	}

	/**
	 * Prints the message to the console if verbose mode is enabled.
	 * 
	 * @param message {@link String} the verbose message
	 */
	private static void printVerbose(String message) {
		if (verboseMode) {
			System.out.println(message);
		}
	}

	/**
	 * Collects the files and URL to be downloaded in the next step...
	 * 
	 * @throws IllegalStateException
	 */
	private static void prepareDownload() throws IllegalStateException {
		System.out.println("#3 - Collecting required files to be downloaded.");
		if (series.size() == 0) {
			System.out.println("Nothing to process, no series found.");
		}

		RssReader reader = new RssReader();
		series.forEach(s -> {
			if (0 != s.getId() && s.isSubDownloadRequired()) {
				try {
					StringBuffer seriesRSSURL = new StringBuffer("https://www.feliratok.info/?ny=magyar&rss=")
							.append(s.getId());
					List<Item> subs = reader.read(seriesRSSURL.toString()).collect(Collectors.toList());

					printVerbose(String.format(
							"\t\t - Fetching RSS URL for series %s (mapping ID: %d), episode %s, releaser: %s)",
							s.getTitle(), s.getId(), s.getUpperCombinedSandE(false), s.getReleaser()));
					for (Item sub : subs) {
						String RSSItem = sub.getTitle().get().toUpperCase().replaceAll(":", "");

						if (RSSItem.contains(s.getUpperCombinedSandE(true)) && RSSItem.contains(s.getUpperReleaser())) {
							downloadables.put(s, sub.getLink().get());
							printVerbose("\t\t - Found a matching subtitle file!");
							printVerbose(String.format("\t\t\t - Download URL: %s", sub.getLink().get()));
							break;
						}
					}

				} catch (IOException e) {
					throw new IllegalStateException("Network or URL error occured, terminatenig...");
				}
			} else {
				printVerbose(
						String.format("\t\t - Skipping series with ID %s because it has a valid subtitle.", s.getId()));
			}
		});

		printVerbose(String.format("\t - A total of %s files will be downloaded.", downloadables.size()));
		System.out.println("#3 - Collecting required files to be downloaded: FINISHED!\n\n");
	}

	private static void downloadFiles() throws IllegalStateException {
		System.out.println("#4 - Downloading prepared files...");

		if (downloadables.size() == 0) {
			System.out.println(
					"\tNothing will be downloaded beacuse there is/are no prepared files. Check back later! :)");
		} else {
			try {
				Path savePath = null;
				SeriesInfo si = null;
				for (Map.Entry<Object, String> e : downloadables.entrySet()) {
					si = (SeriesInfo) e.getKey();
					savePath = Paths.get(si.getSubFileName());

					URL obj = new URL(e.getValue());
					HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
					conn.setReadTimeout(5000);
					conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
					conn.addRequestProperty("User-Agent", "Mozilla");

					boolean redirect = false;
					// normally, 3xx is redirect
					int status = conn.getResponseCode();
					if (status != HttpURLConnection.HTTP_OK) {
						if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
								|| status == HttpURLConnection.HTTP_SEE_OTHER)
							redirect = true;
					}

					if (redirect) {
						// get redirect url from "location" header field
						String newUrl = conn.getHeaderField("Location");

						// get the cookie if need, for login
						String cookies = conn.getHeaderField("Set-Cookie");

						// open the new connnection again
						conn = (HttpURLConnection) new URL(newUrl).openConnection();
						conn.setRequestProperty("Cookie", cookies);
						conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
						conn.addRequestProperty("User-Agent", "Mozilla");
					}

					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String inputLine;
					StringBuffer html = new StringBuffer();

					while ((inputLine = in.readLine()) != null) {
						html.append(inputLine);
					}
					in.close();

					Pattern downloadLinkPattern = Pattern
							.compile("action=letolt[&|&amp;]fnev=(.*)[&|&amp;]felirat=([0-9]+)");
					Matcher matcher = downloadLinkPattern.matcher(html.toString());

					if (matcher.find()) {
						URL downloadURL = new URL("https://feliratok.info/index.php?action=letolt&fnev="
								+ URLEncoder.encode(matcher.group(1), StandardCharsets.UTF_8.toString()) + "&felirat="
								+ matcher.group(2));

						BufferedInputStream inputStream = new BufferedInputStream(downloadURL.openStream());
						printVerbose(String.format("\t - Downloading file from URL: %s", downloadURL));

						if (testMode) {
							System.out.println(
									String.format("\t\tFile would be downloaded to location: %s", savePath.toString()));
						} else {
							FileOutputStream fileOS = new FileOutputStream(savePath.toString());

							byte data[] = new byte[1024];
							int byteContent;
							while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
								fileOS.write(data, 0, byteContent);
							}

							fileOS.close();
							inputStream.close();

							printVerbose(String.format("\t\tFile successfully downloaded to: %s", savePath.toString()));
						}
					}
				}
			} catch (Exception e) {
				throw new IllegalStateException("Could not fetch or process HTTP actions! Terminating...", e);
			}
		}

		System.out.println("#4 - Downloading prepared files: FINISHED.");
	}

	private static void disableSslVerification() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {

				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (

		NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}
}
