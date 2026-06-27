package com.cityride.admin;

import com.cityride.booking.BookingMapper;
import com.cityride.booking.BookingRepository;
import com.cityride.booking.dto.BookingResponse;
import com.cityride.ride.RideMapper;
import com.cityride.ride.RideRepository;
import com.cityride.ride.dto.RideResponse;
import com.cityride.user.User;
import com.cityride.user.UserMapper;
import com.cityride.user.UserService;
import com.cityride.user.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserService userService;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;

    public AdminService(UserService userService, RideRepository rideRepository, BookingRepository bookingRepository) {
        this.userService = userService;
        this.rideRepository = rideRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userService.findAll().stream().map(UserMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RideResponse> listRides() {
        return rideRepository.findAll().stream().map(RideMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listBookings() {
        return bookingRepository.findAll().stream().map(BookingMapper::toResponse).toList();
    }

    @Transactional
    public UserResponse setBlocked(Long userId, boolean blocked) {
        User user = userService.getEntity(userId);
        user.setBlocked(blocked);
        return UserMapper.toResponse(userService.save(user));
    }
}
