def PORT = 8601
def HOST = "danmu.douyutv.com"
def ROOM = "525"


connectAndRead(HOST, PORT, ROOM)

void connectAndRead(host, port, room) {
    def socket = new Socket(host, port)
    socket.setKeepAlive(true)

    def buffer = new byte[8 * 1024]

    socket.withStreams { input, output ->
        def message = new Message("type@=loginreq/username@=/password@=/roomid@=" + room + "/ct@=0/")
        output << message.getBytes()

        message = new Message("type@=joingroup/rid@=" + room + "/gid@=-9999/")
        output << message.getBytes()
        def regex = "type@=chatmsg/.*rid@=(.*?)/.*uid@=(.*?).*nn@=(.*?)/txt@=(.*?)/(.*)/level@=(.*?)/(.*)/";
        while ((len = input.read(buffer)) != -1) {
            def content = splitResponse(Arrays.copyOf(buffer, len))
            content.each {
                def matcher = it =~ regex
                if (matcher) {
                    println matcher[0][3] + "(lv:" + matcher[0][6] + ")" + ": " + matcher[0][4]
                }
            }

        }
    }

    if (!socket.isConnected() || socket.isClosed()) {
        connectAndRead(host, port, room)
    }
}

String[] splitResponse(byte[] buffer) {
    if (buffer == null || buffer.length <= 0) return null

    def resList = []
    def byteArray = buffer.encodeHex().toString().toLowerCase()

    def responseStrings = byteArray.split("b2020000")
    def end
    for (i = 1; i < responseStrings.length; i++) {
        if (!responseStrings[i].contains("00")) continue
        end = responseStrings[i].indexOf("00")
        def bytes = responseStrings[i].substring(0, end).decodeHex()
        if (bytes != null) resList.add(new String(bytes))
    }

    return resList
}

class Message implements Serializable {
    def len
    def code
    def magic
    def content
    def end

    Message(content) {
        len = [calcMessageLength(content), 0x00, 0x00, 0x00]
        code = [calcMessageLength(content), 0x00, 0x00, 0x00]
        magic = [0xb1, 0x02, 0x00, 0x00]
        this.content = content
        end = [0x00]
    }

    int calcMessageLength(content) {
        return 4 + 4 + (content == null ? 0 : content.length()) + 1
    }

    byte[] getBytes() throws IOException {
        def baos = new ByteArrayOutputStream();
        baos.reset();
        for (int b : len) baos.write(b);
        for (int b : code) baos.write(b);
        for (int b : magic) baos.write(b);
        if (content != null) baos.write(content.getBytes());
        for (int b : end) baos.write(b);

        return baos.toByteArray();
    }
}