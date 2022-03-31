package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "street", target = "street")
    @Mapping(source = "streetNo", target = "streetNo")
    @Mapping(source = "zipCode", target = "zipCode")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "licensePlate", target = "licensePlate")
    @Mapping(source = "creditCardNumber", target = "creditCardNumber")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "isManager", target = "isManager")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "street", target = "street")
    @Mapping(source = "streetNo", target = "streetNo")
    @Mapping(source = "zipCode", target = "zipCode")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "licensePlate", target = "licensePlate")
    @Mapping(source = "creditCardNumber", target = "creditCardNumber")
    @Mapping(source = "token", target = "token")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "street", target = "street")
    @Mapping(source = "streetNo", target = "streetNo")
    @Mapping(source = "zipCode", target = "zipCode")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "licensePlate", target = "licensePlate")
    @Mapping(source = "creditCardNumber", target = "creditCardNumber")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

    @Mapping(source = "billingId", target = "billingId")
    @Mapping(source = "bookingType", target = "bookingType")
    @Mapping(source = "bookingId", target = "bookingId")
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    BillingGetDTO convertEntityToBillingGetDTO(Billing billing);

    @Mapping(source = "carparkId", target = "carparkId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "maxCapacity", target = "maxCapacity")
    @Mapping(source = "numOfEmptySpaces", target = "numOfEmptySpaces")
    @Mapping(source = "street", target = "street")
    @Mapping(source = "streetNo", target = "streetNo")
    @Mapping(source = "zipCode", target = "zipCode")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "longitude", target = "longitude")
    @Mapping(source = "latitude", target = "latitude")
    @Mapping(source = "weekdayOpenFrom", target = "weekdayOpenFrom")
    @Mapping(source = "weekdayOpenTo", target = "weekdayOpenTo")
    @Mapping(source = "weekendOpenFrom", target = "weekendOpenFrom")
    @Mapping(source = "weekendOpenTo", target = "weekendOpenTo")
    @Mapping(source = "hourlyTariff", target = "hourlyTariff")
    @Mapping(source = "remarks", target = "remarks")
    CarparkGetDTO convertEntityToCarparkGetDTO(Carpark carpark);

    @Mapping(source = "notificationId", target = "notificationId")
    @Mapping(source = "requesterId", target = "requesterId")
    @Mapping(source = "requestedId", target = "requestedId")
    @Mapping(source = "billingId", target = "billingId")
    @Mapping(source = "response", target = "response")
    NotificationGetDTO convertEntityToNotificationGetDTO(Notification notification);


    @Mapping(source = "parkingslipId", target = "parkingslipId")
    @Mapping(source = "carparkId", target = "carparkId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "checkinDate", target = "checkinDate")
    @Mapping(source = "checkinTime", target = "checkinTime")
    @Mapping(source = "checkoutDate", target = "checkoutDate")
    @Mapping(source = "checkoutTime", target = "checkoutTime")
    @Mapping(source = "licensePlate", target = "licensePlate")
    @Mapping(source = "parkingFee", target = "parkingFee")
    ParkingslipGetDTO convertEntityToParkingslipGetDTO(Parkingslip parkingslip);


    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "carparkId", target = "carparkId")
    @Mapping(source = "checkinDate", target = "checkinDate")
    @Mapping(source = "checkinTime", target = "checkinTime")
    @Mapping(source = "checkoutDate", target = "checkoutDate")
    @Mapping(source = "checkoutTime", target = "checkoutTime")
    Reservation convertReservationPostDTOtoEntity(ReservationPostDTO reservationPostDTO);


    @Mapping(source = "reservationId", target = "reservationId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "carparkId", target = "carparkId")
    @Mapping(source = "checkinDate", target = "checkinDate")
    @Mapping(source = "checkinTime", target = "checkinTime")
    @Mapping(source = "checkoutDate", target = "checkoutDate")
    @Mapping(source = "checkoutTime", target = "checkoutTime")
    @Mapping(source = "licensePlate", target = "licensePlate")
    @Mapping(source = "parkingFee", target = "parkingFee")
    ReservationGetDTO convertEntityToReservationGetDTO(Reservation reservation);


    @Mapping(source = "reservationId", target = "reservationId")
    @Mapping(source = "checkinDate", target = "checkinDate")
    @Mapping(source = "checkinTime", target = "checkinTime")
    @Mapping(source = "checkoutDate", target = "checkoutDate")
    @Mapping(source = "checkoutTime", target = "checkoutTime")
    Reservation convertReservationPutDTOtoEntity(ReservationPutDTO reservationPutDTO);

}
