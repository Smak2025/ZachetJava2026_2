import ru.smak.net.ProtocolConstants;
import ru.smak.net.Server;

void main() {
    var s = new Server(ProtocolConstants.DEFAULT_PORT);
    s.start();
}