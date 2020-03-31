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
	private int series;
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
			String title, int series, int episode, String quality, String releaser) {
		super();
		this.id = id;
		this.foldername = foldername;
		this.videoFileName = videoFileName;
		this.subFileName = subFileName;
		this.subDownloadRequired = subDownloadRequired;
		this.series = series;
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

	public int getSeries() {
		return series;
	}

	public void setSeries(int series) {
		this.series = series;
	}

	public void setSeries(String series) {
		try {
			this.series = Integer.parseInt(series);
		} catch (NumberFormatException e) {
			this.series = -1;
		}
	}

	public int getEpisode() {
		return episode;
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}

	public void setEpisode(String episode) {
		try {
			this.episode = Integer.parseInt(episode);
		} catch (NumberFormatException e) {
			this.series = -1;
		}
	}

	public String getUpperCombinedSandE() {
		return (series + "x" + String.format("%02d", episode)).toUpperCase();
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
		result = prime * result + series;
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
		if (series != other.series)
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
		return "\nSeriesInfo [\n\tid = " + id + "\n\tfoldername = " + foldername + "\n\t video file name = "
				+ videoFileName + "\n\t subtitle file name = " + subFileName + "\n\ttitle = " + title + "\n\tseries = "
				+ series + "\n\tepisode = " + episode + "\n\tquality=" + quality + "\n\treleaser=" + releaser + "\n]";
	}

}
