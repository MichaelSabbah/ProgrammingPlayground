package playground.logic.jpa;

import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexmo.client.NexmoClient;
import com.nexmo.client.sms.SmsSubmissionResponse;
import com.nexmo.client.sms.SmsSubmissionResponseMessage;
import com.nexmo.client.sms.messages.TextMessage;

import playground.dal.UserDao;
import playground.logic.Entities.User.UserEntity;
import playground.logic.exceptions.conflict.UserAlreadyExistsException;
import playground.logic.exceptions.internal.InternalErrorException;
import playground.logic.exceptions.notfound.UserNotFoundException;
import playground.logic.helpers.PlaygroundConsts;
import playground.logic.services.ISmsService;

@Service
public class SmsService implements ISmsService {

	private NexmoClient client;
	private UserDao userDao;

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@PostConstruct
	public void init()
	{
		this.client = new NexmoClient.Builder()
				.apiKey("1d7185b1")
				.apiSecret("0acd3a8d32da645c")
				.build();
	}


	@Override
	public void sendSMS(String email,String playground,String to) throws Throwable {

		SmsSubmissionResponse responses;
		List<UserEntity> userEntities =this.userDao.findByEmailAndPlayground(email, playground);
		UserEntity userEntity;
		int verificationCode;

		if(userEntities.isEmpty())
		{
			throw new UserNotFoundException("USER_NOT_FOUND");
		}
		userEntity = userEntities.get(0);
		verificationCode = userEntity.getConfirmCode();
		if(verificationCode == -1)
		{
			throw new UserAlreadyExistsException("USER_ALREADY_EXISTS");
		}

		try {
			String normalizeTo = to;
			if(normalizeTo.startsWith("0"))
			{
				normalizeTo ="+972"+ normalizeTo.substring(1);
			}

			responses = client.getSmsClient().submitMessage(new TextMessage(
					PlaygroundConsts.APP_NAME,
					normalizeTo,
					PlaygroundConsts.MESSAGE+verificationCode));

		} catch (Exception ex) {
			throw new InternalErrorException(ex.getMessage());
		}
	}



}
