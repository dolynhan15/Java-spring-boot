package com.qooco.boost.services.oracle.impl;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/5/2018 - 6:06 PM
 */

import com.qooco.boost.data.oracle.entities.UserPreviousPosition;
import com.qooco.boost.data.oracle.reposistories.UserPreviousPositionRepository;
import com.qooco.boost.data.oracle.services.UserPreviousPositionService;
import com.qooco.boost.data.oracle.services.impl.UserPreviousPositionServiceImpl;
import com.qooco.boost.exception.EntityNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(PowerMockRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserPreviousPositionServiceImplTest {

    @InjectMocks
    private UserPreviousPositionService userPreviousPositionService = new UserPreviousPositionServiceImpl();

    @Mock
    private UserPreviousPositionRepository userPreviousPositionRepository = Mockito.mock(UserPreviousPositionRepository.class);

    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void save_whenInputRight_thenReturnSuccess(){
        UserPreviousPosition userPreviousPosition = new UserPreviousPosition();

        Mockito.when(userPreviousPositionRepository.save(userPreviousPosition)).thenReturn(new UserPreviousPosition((long)12345));
        Assert.assertEquals(new UserPreviousPosition((long)12345), userPreviousPositionService.save(userPreviousPosition));
    }

    @Test
    public void findById_whenInputRightId_theReturnSuccess(){
        UserPreviousPosition userPreviousPosition = new UserPreviousPosition();
        userPreviousPosition.setId((long)12345);
        Mockito.when(userPreviousPositionRepository.findByUserPreviousPositionId(12345)).thenReturn(userPreviousPosition);
        Assert.assertEquals(userPreviousPosition, userPreviousPositionService.findById((long)12345));
    }

    @Test
    public void findById_whenInputWrongId_theReturnFail(){
        UserPreviousPosition userPreviousPosition = new UserPreviousPosition();
        userPreviousPosition.setId((long)12345);
        Mockito.when(userPreviousPositionRepository.findByUserPreviousPositionId(12345)).thenReturn(userPreviousPosition);
        Assert.assertNotEquals(userPreviousPosition, userPreviousPositionService.findById((long)123456));
    }

    @Test
    public void deleteUserPreviousPositions_whenInputRightId_thenReturnSuccess(){

        Mockito.doNothing().when(userPreviousPositionRepository).deleteById((long)12345);
        userPreviousPositionService.deleteUserPreviousPositionById((long)12345);
    }

    @Test
    public void deleteUserPreviousPositions_whenInputWrongId_thenThrowsException(){
        Mockito.doThrow(EntityNotFoundException.class).when(userPreviousPositionRepository).deleteById((long)12345);
        try {
            userPreviousPositionService.deleteUserPreviousPositionById((long) 12345);
        } catch (EntityNotFoundException ex){

        }
    }
}
