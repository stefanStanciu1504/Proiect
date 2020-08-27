package pro.xstore.api.sync;

import java.util.Map;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.records.*;
import pro.xstore.api.message.response.*;
import pro.xstore.api.streaming.StreamingListener;
import pro.xstore.api.sync.ServerData.ServerEnum;

public class Example {
	private LoginResponse loginResponse;
	private SyncAPIConnector connector;
	private StreamingListener sl;

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
				sl = new StreamingListener();

				connector.connectStream(sl);
				connector.subscribeBalance();
				Thread.sleep(250);
				SBalanceRecord balance = connector.getBalanceRecord();
				if (balance != null)
					System.out.println(balance.getBalance());
				connector.unsubscribeBalance();
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	protected Map<String,Server> getAvailableServers() {
		return ServerData.getProductionServers();
	}
}