///**
// * TESTS outcommented
// * Need to be adapted to current entities
// */
//
//package ch.uzh.ifi.hase.soprafs22.repository;
//
//import ch.uzh.ifi.hase.soprafs22.entity.User;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//
//import java.time.*;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@DataJpaTest
//public class UserRepositoryIntegrationTest {
//
//  @Autowired
//  private TestEntityManager entityManager;
//
//  @Autowired
//  private UserRepository userRepository;
//
//  @Test
//  public void findByName_success() {
//    // given
//    User user = new User();
//    user.setPassword("password");
//    user.setUsername("firstname@lastname");
////    user.setLogged_in(true);
//    user.setToken("1");
////    user.setCreationDate(LocalDate.now());
//
//    entityManager.persist(user);
//    entityManager.flush();
//
//    // when
//    User found = userRepository.findByUsername(user.getUsername());
//
//    // then
//    assertNotNull(found.getUserId());
//    assertEquals(found.getPassword(), user.getPassword());
//    assertEquals(found.getUsername(), user.getUsername());
//    assertEquals(found.getToken(), user.getToken());
////    assertEquals(found.getLogged_in(), user.getLogged_in());
//  }
//}
