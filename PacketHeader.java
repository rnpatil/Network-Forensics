import java.util.Arrays;

public class PacketHeader {

	private  long size=0;
	private  long time=0;

	PacketHeader(final byte[] headerData)
	{
		this.size = littleEndian(Arrays.copyOfRange(headerData, 8, 12)); 
		this.time=(littleEndian(Arrays.copyOfRange(headerData, 0, 4))*1000000)+littleEndian(Arrays.copyOfRange(headerData, 4,8));
	
	}

	public static long littleEndian(byte[] bytes) {
		long ret = 0;
		for (int i = bytes.length - 1; i >= 0; i--) {
			ret <<= 8;
			ret |= (int) bytes[i] & 0xFF;
		}
		
		return ret;
	}

	public  long getSize() {
		return size;
	}

	public  long getTime() {
		return time;
	}


}