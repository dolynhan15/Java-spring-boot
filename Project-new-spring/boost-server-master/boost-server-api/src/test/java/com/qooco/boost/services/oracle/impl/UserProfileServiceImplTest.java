package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.reposistories.UserProfileRepository;
import com.qooco.boost.data.oracle.services.UserProfileService;
import com.qooco.boost.data.oracle.services.impl.UserProfileServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 6/25/2018 - 1:49 PM
 */
@RunWith(PowerMockRunner.class)
public class UserProfileServiceImplTest {

	@InjectMocks
	private UserProfileService userProfileService = new UserProfileServiceImpl();

	@Mock
	private UserProfileRepository userProfileRepository = Mockito.mock(UserProfileRepository.class);

	@Test
	public void saveUserProfile_whenHaveValidUserProfile_thenReturnUserProfileResp() {
		UserProfile userProfile = new UserProfile(1000L, "longntran");

		Mockito.when(userProfileRepository.save(userProfile))
				.thenReturn(userProfile);

		UserProfile actualResp = userProfileService.save(userProfile);

		Assert.assertEquals(userProfile, actualResp);
		Assert.assertEquals(userProfile.getUsername(), actualResp.getUsername());
	}

	@Test
	public void findByUsername_whenHaveValidUsername_thenReturnUserProfileResp() {
		String validUsername = "longntran";
		UserProfile userProfile = new UserProfile(1000L, validUsername);

		Mockito.when(userProfileRepository.findByUsernameIgnoreCase(validUsername))
				.thenReturn(userProfile);

		UserProfile actualResp = userProfileService.findByUsername(validUsername);
		Assert.assertEquals(userProfile, actualResp);
	}


	@Test
	public void findByUsername_whenHaveInvalidUsername_thenReturnUserProfileResp() {
		String inValidUsername = "longntran111";
		UserProfile userProfile = new UserProfile(1000L, "longntran");

		Mockito.when(userProfileRepository.findByUsernameIgnoreCase("longntran"))
				.thenReturn(userProfile);

		UserProfile actualResp = userProfileService.findByUsername(inValidUsername);
		Assert.assertNotEquals(userProfile, actualResp);
	}

	@Test
	public void isExist_whenHaveValidUserProfileId_thenReturnTrueResp() {
		Mockito.when(userProfileRepository.existsById(1000L))
				.thenReturn(true);

		boolean actualResp = userProfileService.isExist(1000L);
		Assert.assertTrue(actualResp);
	}

	@Test
	public void isExist_whenHaveInvalidUserProfileId_thenReturnFalseResp() {
		Mockito.when(userProfileRepository.existsById(1000L))
				.thenReturn(false);

		boolean actualResp = userProfileService.isExist(1000L);
		Assert.assertFalse(actualResp);
	}
}