package pro.xstore.api.sync;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.response.*;
import pro.xstore.api.streaming.StreamingListener;
import pro.xstore.api.sync.ServerData.ServerEnum;

public class Example {
	private LoginResponse loginResponse;
	private SyncAPIConnector connector;

	public LoginResponse getLoginResponse() {
		return loginResponse;
	}

	public SyncAPIConnector getConnector() {
		return connector;
	}

	public void runExample(ServerEnum server, Credentials credentials) throws Exception {
		try {
			connector = new SyncAPIConnector(server);
			loginResponse = APICommandFactory.executeLoginCommand(connector, credentials);
			if (loginResponse.getStatus())
			{
				StreamingListener sl = new StreamingListener();

				connector.connectStream(sl);
				connector.subscribeBalance();
				connector.unsubscribeBalance();
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}
}