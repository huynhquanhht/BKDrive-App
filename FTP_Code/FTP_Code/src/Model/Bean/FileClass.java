package Model.Bean;
import javafx.scene.image.ImageView;
import java.io.Serializable;

@SuppressWarnings("serial")
public class FileClass  implements Serializable {
	private ImageView img;
	private String name;
	private String lastModified;
	private String size;
	private String type;
	private String path;
	private byte[] file;
	public FileClass() {
		super();
	}
	public FileClass(ImageView img, String name, String lastModified, String size, String type, String path, byte[] file) {
		super();
		this.img = img;
		this.name = name;
		this.lastModified = lastModified;
		this.size = size;
		this.type = type;
		this.path = path;
		this.file = file;
	}
	public final ImageView getImg() {
		return img;
	}

	public final void setImg(ImageView img) {
		this.img = img;
	}
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final String getLastModified() {
		return lastModified;
	}
	public final void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public final String getSize() {
		return size;
	}
	public final void setSize(String size) {
		this.size = size;
	}
	public final String getType() {
		return type;
	}
	public final void setType(String type) {
		this.type = type;
	}
	public final String getPath() {
		return path;
	}
	public final void setPath(String path) {
		this.path = path;
	}
	public final byte[] getFile() {
		return file;
	}
	public final void setFile(byte[] file) {
		this.file = file;
	}


	
}
