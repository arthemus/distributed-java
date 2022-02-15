package io.distributed;

import io.distributed.helpers.PCDPFilesystem;
import io.distributed.helpers.PCDPPath;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServerThread {

	private ExecutorService executorService;

	/**
	 * Main entrypoint for the basic file server.
	 *
	 * @param socket Provided socket to accept connections on.
	 * @param fs     A proxy filesystem to serve files from. See the PCDPFilesystem
	 *               class for more detailed documentation of its usage.
	 * @param nCores The number of cores that are available to your
	 *               multi-threaded file server. Using this argument is entirely
	 *               optional. You are free to use this information to change
	 *               how you create your threads, or ignore it.
	 * @throws IOException If an I/O error is detected on the server. This
	 *                     should be a fatal error, your file server
	 *                     implementation is not expected to ever throw
	 *                     IOExceptions during normal operation.
	 */
	public void run(final ServerSocket socket, final PCDPFilesystem fs, final int nCores) throws IOException {
		if (executorService == null)
			this.executorService = Executors.newFixedThreadPool(nCores);

		while (true) {
			final Socket accept = socket.accept();
			this.executorService.execute(() -> {
				try (final InputStream inputStream = accept.getInputStream();
					 final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					 final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

					final String line = bufferedReader.readLine();
					assert line != null;
					assert line.startsWith("GET");

					try (final OutputStream outputStream = accept.getOutputStream();
						 final PrintWriter printWriter = new PrintWriter(outputStream)) {

						final String path = line.split(" ")[1];
						final String content = fs.readFile(new PCDPPath(path));
						if (content == null) {
							printWriter.write("HTTP/1.0 404 Not Found\r\n");
							printWriter.write("\r\n");
						} else {
							printWriter.write("HTTP/1.0 200 OK\r\n");
							printWriter.write("\r\n");
							printWriter.write(content);
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}
}
