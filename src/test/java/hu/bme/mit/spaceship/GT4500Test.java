package hu.bme.mit.spaceship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

public class GT4500Test {

  private TorpedoStore mockPrim;
  private TorpedoStore mockSec;
  private GT4500 ship;

  @BeforeEach
  public void init(){
    mockPrim = mock(TorpedoStore.class);
    mockSec = mock(TorpedoStore.class);
    this.ship = new GT4500(mockPrim, mockSec);
  }

  @Test
  public void fireTorpedo_Single_Success(){
    // Arrange
    when(mockPrim.isEmpty()).thenReturn(false);

    when(mockPrim.fire(1)).thenReturn(true);

    // Act
    ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    verify(mockPrim, times(1)).isEmpty();
    verify(mockPrim, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_All_Success(){
    // Arrange
    when(mockPrim.isEmpty()).thenReturn(false);
    when(mockSec.isEmpty()).thenReturn(false);

    when(mockPrim.fire(1)).thenReturn(true);
    when(mockSec.fire(1)).thenReturn(true);

    // Act
    ship.fireTorpedo(FiringMode.ALL);

    // Assert
    verify(mockPrim, times(1)).isEmpty();
    verify(mockPrim, times(1)).fire(1);

    verify(mockSec, times(1)).isEmpty();
    verify(mockSec, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_Single_Twice() {
    // Arrange
    when(mockPrim.isEmpty()).thenReturn(false);
    when(mockSec.isEmpty()).thenReturn(false);
    
    when(mockPrim.fire(1)).thenReturn(true);
    when(mockSec.fire(1)).thenReturn(true);

    // Act
    boolean success = ship.fireTorpedo(FiringMode.SINGLE);
    success = success && ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    verify(mockPrim, times(1)).isEmpty();
    verify(mockPrim, times(1)).fire(1);

    verify(mockSec, times(1)).isEmpty();
    verify(mockSec, times(1)).fire(1);

    assertEquals(true, success);
  }

  @Test
  public void fireTorpedo_Single_Twice_WhenSecondStoreEmpty() {
    // Arrange
    when(mockPrim.isEmpty()).thenReturn(false);
    when(mockSec.isEmpty()).thenReturn(true);
    
    when(mockPrim.fire(1)).thenReturn(true);

    // Act
    boolean success = ship.fireTorpedo(FiringMode.SINGLE);
    success = success && ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    verify(mockPrim, times(2)).isEmpty();
    verify(mockPrim, times(2)).fire(1);

    verify(mockSec, times(1)).isEmpty();
    verify(mockSec, never()).fire(1);

    assertEquals(true, success);
  }

  @Test
  public void fireTorpedo_Single_Once_WhenBothStoresEmpty() {
    // Arrange
    when(mockPrim.isEmpty()).thenReturn(true);
    when(mockSec.isEmpty()).thenReturn(true);

    // Act
    boolean success = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    verify(mockPrim, times(1)).isEmpty();
    verify(mockPrim, never()).fire(1);

    verify(mockSec, times(1)).isEmpty();
    verify(mockSec, never()).fire(1);

    assertEquals(false, success);
  }

  @Test
  public void fireTorpedo_Single_Twice_WhenFirstStoreError() {
    // Arrange
    when(mockPrim.isEmpty()).thenReturn(false);

    when(mockPrim.fire(1)).thenReturn(false);

    // Act
    boolean success = ship.fireTorpedo(FiringMode.SINGLE);
    success = success && ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    verify(mockPrim, times(1)).isEmpty();
    verify(mockPrim, times(1)).fire(1);

    verify(mockSec, never()).isEmpty();
    verify(mockSec, never()).fire(1);

    assertEquals(false, success);
  }

  @Test
  public void fireTorpedo_All_WhenFirstStoreEmpty() {
    // Arrange
    when(mockPrim.isEmpty()).thenReturn(true);

    // Act
    boolean success = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    verify(mockPrim, times(1)).isEmpty();
    verify(mockPrim, never()).fire(1);

    verify(mockSec, never()).isEmpty();
    verify(mockSec, never()).fire(1);

    assertEquals(false, success);
  }

  @Test
  public void fireTorpedo_DefaultFiringMode() {
    // Arrange
    
    // Act
    boolean success = ship.fireTorpedo(FiringMode.DEFAULT);
    
    // Assert
    verify(mockPrim, never()).isEmpty();
    verify(mockPrim, never()).fire(1);
    
    verify(mockSec, never()).isEmpty();
    verify(mockSec, never()).fire(1);
    
    assertEquals(false, success);
  }

  @Test
  public void fireTorpedo_Single_Twice_WhenSecondStoreEmpty_FirstStoreRunsOut() {
    // Arrange
    when(mockPrim.isEmpty()).thenAnswer(new Answer<Boolean>() {
      private int count = 0;
  
      public Boolean answer(InvocationOnMock invocation) {
          if (count++ == 1)
              return true;
  
          return false;
      }
    });

    when(mockSec.isEmpty()).thenReturn(true);
    when(mockPrim.fire(1)).thenReturn(true);

    // Act
    boolean success = ship.fireTorpedo(FiringMode.SINGLE);
    success = success && ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    verify(mockPrim, times(2)).isEmpty();
    verify(mockPrim, times(1)).fire(1);
    
    verify(mockSec, times(1)).isEmpty();
    verify(mockSec, never()).fire(1);
    
    assertEquals(false, success);
  }

  @Test
  public void fireTorpedo_Single_ThreeTimes_WhenFirstStoreRunsOut() {
    // Arrange
    when(mockPrim.isEmpty()).thenAnswer(new Answer<Boolean>() {
      private int count = 0;
  
      public Boolean answer(InvocationOnMock invocation) {
          if (count++ == 1)
              return true;
  
          return false;
      }
    });

    when(mockSec.isEmpty()).thenReturn(false);

    when(mockPrim.fire(1)).thenReturn(true);
    when(mockSec.fire(1)).thenReturn(true);

    // Act
    boolean success = ship.fireTorpedo(FiringMode.SINGLE);
    success = success && ship.fireTorpedo(FiringMode.SINGLE);
    success = success && ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    verify(mockPrim, times(2)).isEmpty();
    verify(mockPrim, times(1)).fire(1);
    
    verify(mockSec, times(2)).isEmpty();
    verify(mockSec, times(2)).fire(1);
    
    assertEquals(true, success);
  }

  @Test
  public void fireTorpedo_All_WhenSecondStoreEmpty() {
    // Arrange
    when(mockPrim.isEmpty()).thenReturn(false);
    when(mockSec.isEmpty()).thenReturn(true);

    // Act
    boolean success = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    verify(mockPrim, times(1)).isEmpty();
    verify(mockPrim, never()).fire(1);

    verify(mockSec, times(1)).isEmpty();
    verify(mockSec, never()).fire(1);

    assertEquals(false, success);
  }

  @Test
  public void fireTorpedo_All_WhenFirstStoreError() {
    // Arrange
    when(mockPrim.isEmpty()).thenReturn(false);
    when(mockSec.isEmpty()).thenReturn(false);

    when(mockPrim.fire(1)).thenReturn(false);    

    // Act
    boolean success = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    verify(mockPrim, times(1)).isEmpty();
    verify(mockPrim, times(1)).fire(1);

    verify(mockSec, times(1)).isEmpty();
    verify(mockSec, never()).fire(1);

    assertEquals(false, success);
  }

  @Test
  public void fireTorpedo_All_WhenSecondStoreError() {
    // Arrange
    when(mockPrim.isEmpty()).thenReturn(false);
    when(mockSec.isEmpty()).thenReturn(false);

    when(mockPrim.fire(1)).thenReturn(true);
    when(mockSec.fire(1)).thenReturn(false);

    // Act
    boolean success = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    verify(mockPrim, times(1)).isEmpty();
    verify(mockPrim, times(1)).fire(1);

    verify(mockSec, times(1)).isEmpty();
    verify(mockSec, times(1)).fire(1);

    assertEquals(false, success);
  }
}
