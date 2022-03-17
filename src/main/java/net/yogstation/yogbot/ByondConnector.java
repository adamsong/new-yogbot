package net.yogstation.yogbot;

import net.yogstation.yogbot.config.ByondConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByondConnector {

	private final ByondConfig config;

	public ByondConnector() {
		config = Yogbot.config.byondConfig;
	}

	public Object request(String query) {
		query += "&key=" + config.serverKey;

		ByteBuffer buffer = ByteBuffer.allocate(query.length() + 10);
		buffer.put(new byte[]{0x00, (byte) 0x83});
		buffer.putShort((short) (query.length() + 6));
		buffer.put(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00});
		buffer.put(query.getBytes(StandardCharsets.UTF_8));
		buffer.put((byte) 0x00);

		try (Socket socket = new Socket(config.serverAddress, config.serverPort)){
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(buffer.array());

			InputStream inputStream = socket.getInputStream();
			byte[] headerBuffer = new byte[4];
			if(inputStream.read(headerBuffer) < 4) return null;
			//noinspection ConstantConditions // intellij linter doesn't understand out paramters
			if(headerBuffer[0] != 0x00 || headerBuffer[1] != 0x83) return null;
			short size = ByteBuffer.wrap(headerBuffer, 2, 2).getShort();

			byte[] bodyBuffer = new byte[size];
			if(inputStream.read(bodyBuffer) < size) return null;

			ByteBuffer bb = ByteBuffer.wrap(bodyBuffer);
			byte type = bb.get();
			if(type == 0x2A)
				return bb.getFloat();
			if(type == 0x06) {
				return StandardCharsets.UTF_8.decode(bb);
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
