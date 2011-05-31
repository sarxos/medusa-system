package com.sarxos.medusa.comm;

import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.SignalType;


public class FakeMessagesBroker implements MessagesBroker {

	@Override
	public boolean acknowledge(Paper paper, SignalType type) throws MessagingException {
		return true;
	}

}
