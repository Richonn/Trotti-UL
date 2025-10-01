//package ca.ulaval.glo4003.ws.api.contact;
//
//import ca.ulaval.glo4003.ws.api.contact.dto.ContactDto;
//import ca.ulaval.glo4003.ws.domain.contact.ContactService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.BDDMockito.given;
//
//
//@ExtendWith(MockitoExtension.class)
//public class ContactResourceImplTest {
//  public static final String ID = "id";
//  public static final String PHONE_NUMBER = "phoneNumber";
//  public static final String ADDRESS = "address";
//  public static final String NAME = "name";
//
//  private ContactResource contactResource;
//  @Mock
//  private ContactService contactService;
//
//  @BeforeEach
//  public void setUp() {
//    contactResource = new ContactResourceImpl(contactService);
//  }
//
//  @Test
//  public void whenFindAllContacts_thenDelegateToService() {
//    // given
//    ContactDto contactDto = new ContactDto(ID, PHONE_NUMBER, ADDRESS, NAME);
//    given(contactService.findAllContacts()).willReturn(List.of(contactDto));
//
//    // when
//    List<ContactDto> contactDtos = contactResource.getContacts();
//
//    // then
//    assertThat(contactDtos).containsExactly(contactDto);
//  }
//}
