package hu.burgatshow.sf.main;

import java.io.Serializable;

public class SeriesInfo implements Serializable, Comparable<SeriesInfo> {
	private static final long serialVersionUID = -1775823994470053291L;

	private int id;
	private String foldername;
	private String videoFileName;
	private String subFileName;
	private boolean subDownloadRequired;
	private String title;
	private int season;
	private int episode;
	private String quality;
	private String releaser;

	public SeriesInfo() {
	}

	public SeriesInfo(String foldername) {
		super();
		this.foldername = foldername;
	}

	public SeriesInfo(int id, String foldername, String videoFileName, String subFileName, boolean subDownloadRequired,
			String title, int season, int episode, String quality, String releaser) {
		super();
		this.id = id;
		this.foldername = foldername;
		this.videoFileName = videoFileName;
		this.subFileName = subFileName;
		this.subDownloadRequired = subDownloadRequired;
		this.season = season;
		this.title = title;
		this.episode = episode;
		this.quality = quality;
		this.releaser = releaser;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setId(String id) {
		try {
			this.id = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			this.id = -1;
		}
	}

	public String getFoldername() {
		return foldername;
	}

	public void setFoldername(String foldername) {
		this.foldername = foldername;
	}

	public String getVideoFileName() {
		return videoFileName;
	}

	public void setVideoFileName(String videoFileName) {
		this.videoFileName = videoFileName;
	}

	public String getSubFileName() {
		return subFileName;
	}

	public void setSubFileName(String subFileName) {
		this.subFileName = subFileName;
	}

	public boolean isSubDownloadRequired() {
		return subDownloadRequired;
	}

	public void setSubDownloadRequired(boolean subDownloadRequired) {
		this.subDownloadRequired = subDownloadRequired;
	}

	public String getTitle() {
		return title;
	}

	public String getUpperTitle() {
		return title.toUpperCase();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSeason() {
		return season;
	}

	public String getSeasonFormatted() {
		return String.format("%02d", season);
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public void setSeason(String season) {
		try {
			this.season = Integer.parseInt(season);
		} catch (NumberFormatException e) {
			this.season = -1;
		}
	}

	public int getEpisode() {
		return episode;
	}

	public String getEpisodeFormatted() {
		return String.format("%02d", episode);
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}

	public void setEpisode(String episode) {
		try {
			this.episode = Integer.parseInt(episode);
		} catch (NumberFormatException e) {
			this.season = -1;
		}
	}

	public String getUpperCombinedSandE(boolean type) {
		return type ? (season + "x" + getEpisodeFormatted()).toUpperCase()
				: ("S" + getSeasonFormatted() + "E" + getEpisodeFormatted());
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getReleaser() {
		return releaser;
	}

	public String getUpperReleaser() {
		return releaser.toUpperCase();
	}

	public void setReleaser(String releaser) {
		this.releaser = releaser;
	}

	@Override
	public int compareTo(SeriesInfo seriesInfo) {
		return this.foldername.compareTo(seriesInfo.foldername);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + episode;
		result = prime * result + ((foldername == null) ? 0 : foldername.hashCode());
		result = prime * result + id;
		result = prime * result + ((quality == null) ? 0 : quality.hashCode());
		result = prime * result + ((releaser == null) ? 0 : releaser.hashCode());
		result = prime * result + season;
		result = prime * result + (subDownloadRequired ? 1231 : 1237);
		result = prime * result + ((subFileName == null) ? 0 : subFileName.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((videoFileName == null) ? 0 : videoFileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SeriesInfo other = (SeriesInfo) obj;
		if (episode != other.episode)
			return false;
		if (foldername == null) {
			if (other.foldername != null)
				return false;
		} else if (!foldername.equals(other.foldername))
			return false;
		if (id != other.id)
			return false;
		if (quality == null) {
			if (other.quality != null)
				return false;
		} else if (!quality.equals(other.quality))
			return false;
		if (releaser == null) {
			if (other.releaser != null)
				return false;
		} else if (!releaser.equals(other.releaser))
			return false;
		if (season != other.season)
			return false;
		if (subDownloadRequired != other.subDownloadRequired)
			return false;
		if (subFileName == null) {
			if (other.subFileName != null)
				return false;
		} else if (!subFileName.equals(other.subFileName))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (videoFileName == null) {
			if (other.videoFileName != null)
				return false;
		} else if (!videoFileName.equals(other.videoFileName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SeriesInfo [\n\tid = " + id + ",\n\tfoldername = " + foldername + ",\n\tvideoFileName = "
				+ videoFileName + ",\n\tsubFileName = " + subFileName + ",\n\tsubDownloadRequired = "
				+ subDownloadRequired + ",\n\ttitle = " + title + ",\n\tseason = " + season
				+ ",\n\tepisode = " + episode + ",\n\tquality = " + quality + ",\n\treleaser = " + releaser
				+ "\n]";
	}

	

}
