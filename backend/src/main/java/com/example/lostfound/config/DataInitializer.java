package com.example.lostfound.config;

import com.example.lostfound.dto.ClaimRequestDto;
import com.example.lostfound.dto.FoundItemDto;
import com.example.lostfound.dto.LostItemDto;
import com.example.lostfound.entity.Role;
import com.example.lostfound.entity.User;
import com.example.lostfound.repository.UserRepository;
import com.example.lostfound.service.ClaimService;
import com.example.lostfound.service.FoundItemService;
import com.example.lostfound.service.LostItemService;
import com.example.lostfound.util.PasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LostItemService lostItemService;
    private final FoundItemService foundItemService;
    private final ClaimService claimService;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                           LostItemService lostItemService,
                           FoundItemService foundItemService,
                           ClaimService claimService) {
        this.userRepository = userRepository;
        this.lostItemService = lostItemService;
        this.foundItemService = foundItemService;
        this.claimService = claimService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Data already initialized
        }

        // 1. Create Default Admin User
        User admin = new User();
        admin.setFullName("Campus Administrator");
        admin.setEmail("admin@campus.edu");
        admin.setPhoneNumber("+1 555-0199");
        admin.setStudentEmpId("EMP-1001");
        admin.setPassword(PasswordEncoderUtil.encode("admin123"));
        admin.setRole(Role.ADMIN);
        User savedAdmin = userRepository.save(admin);

        // 2. Create Standard Sample Users
        User john = new User();
        john.setFullName("John Doe");
        john.setEmail("john@student.edu");
        john.setPhoneNumber("+1 555-0144");
        john.setStudentEmpId("STU-8821");
        john.setPassword(PasswordEncoderUtil.encode("user123"));
        john.setRole(Role.USER);
        User savedJohn = userRepository.save(john);

        User alice = new User();
        alice.setFullName("Alice Smith");
        alice.setEmail("alice@employee.edu");
        alice.setPhoneNumber("+1 555-0177");
        alice.setStudentEmpId("EMP-4402");
        alice.setPassword(PasswordEncoderUtil.encode("user123"));
        alice.setRole(Role.USER);
        User savedAlice = userRepository.save(alice);

        // 3. Sample Lost Items
        LostItemDto lost1 = new LostItemDto();
        lost1.setItemName("Black Leather Wallet");
        lost1.setCategory("Wallets");
        lost1.setDescription("Black bifold leather wallet containing driver license and student ID");
        lost1.setDateLost(LocalDate.now().minusDays(3));
        lost1.setTimeLost(LocalTime.of(14, 30));
        lost1.setLocationLost("Central Library 2nd Floor");
        lost1.setColor("Black");
        lost1.setBrand("Fossil");
        lost1.setCharacteristics("Has a small scratch on the front edge and driver license inside");
        lost1.setContactInfo("john@student.edu | +1 555-0144");
        lost1.setImageUrl("https://images.unsplash.com/photo-1627123424574-724758594e93?w=500&auto=format&fit=crop&q=60");
        LostItemDto createdLost1 = lostItemService.reportLostItem(lost1, savedJohn.getId());

        LostItemDto lost2 = new LostItemDto();
        lost2.setItemName("Dell XPS 15 Laptop");
        lost2.setCategory("Electronics");
        lost2.setDescription("Silver 15-inch laptop in grey sleeve with stickers on top");
        lost2.setDateLost(LocalDate.now().minusDays(2));
        lost2.setTimeLost(LocalTime.of(10, 15));
        lost2.setLocationLost("Science Building Room 302");
        lost2.setColor("Silver");
        lost2.setBrand("Dell");
        lost2.setCharacteristics("NASA and Python stickers on the top lid");
        lost2.setContactInfo("alice@employee.edu | +1 555-0177");
        lost2.setImageUrl("https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=500&auto=format&fit=crop&q=60");
        lostItemService.reportLostItem(lost2, savedAlice.getId());

        // 4. Sample Found Items (Will automatically receive unique ZXing QR Codes!)
        FoundItemDto found1 = new FoundItemDto();
        found1.setItemName("Black Leather Wallet with IDs");
        found1.setCategory("Wallets");
        found1.setDescription("Found near quiet study desk in Library. Contains credit cards and student ID");
        found1.setDateFound(LocalDate.now().minusDays(3));
        found1.setTimeFound(LocalTime.of(15, 10));
        found1.setLocationFound("Central Library 2nd Floor Desk #12");
        found1.setColor("Black");
        found1.setBrand("Fossil");
        found1.setCharacteristics("Matches lost bifold wallet description");
        found1.setImageUrl("https://images.unsplash.com/photo-1627123424574-724758594e93?w=500&auto=format&fit=crop&q=60");
        FoundItemDto createdFound1 = foundItemService.registerFoundItem(found1, savedAdmin.getId());

        FoundItemDto found2 = new FoundItemDto();
        found2.setItemName("Silver Dell Laptop in Sleeve");
        found2.setCategory("Electronics");
        found2.setDescription("Found on podium after CS Lecture. Has stickers on back");
        found2.setDateFound(LocalDate.now().minusDays(2));
        found2.setTimeFound(LocalTime.of(11, 45));
        found2.setLocationFound("Science Building Hallway");
        found2.setColor("Silver");
        found2.setBrand("Dell");
        found2.setCharacteristics("Stickers on lid");
        found2.setImageUrl("https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=500&auto=format&fit=crop&q=60");
        foundItemService.registerFoundItem(found2, savedAdmin.getId());

        FoundItemDto found3 = new FoundItemDto();
        found3.setItemName("Apple Watch Series 8");
        found3.setCategory("Electronics");
        found3.setDescription("Black sport band smartwatch found on cafeteria table");
        found3.setDateFound(LocalDate.now().minusDays(1));
        found3.setTimeFound(LocalTime.of(13, 0));
        found3.setLocationFound("Student Union Cafeteria Table #4");
        found3.setColor("Black / Silver");
        found3.setBrand("Apple");
        found3.setCharacteristics("Custom wallpaper with initials JD");
        found3.setImageUrl("https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=500&auto=format&fit=crop&q=60");
        foundItemService.registerFoundItem(found3, savedAlice.getId());

        // 5. Sample Ownership Claim Submission
        ClaimRequestDto claim = new ClaimRequestDto();
        claim.setFoundItemId(createdFound1.getId());
        claim.setLostItemId(createdLost1.getId());
        claim.setClaimantName(savedJohn.getFullName());
        claim.setContactDetails(savedJohn.getEmail() + " | " + savedJohn.getPhoneNumber());
        claim.setItemDescription("Fossil Black Bifold Leather Wallet lost in Library on 2nd Floor");
        claim.setIdentifyingDetails("Contains Student ID STU-8821 and a photo of a dog in the window slot");
        claim.setProofDetails("Student ID match and matching lost item report LOST-1001");
        claimService.submitClaim(claim, savedJohn.getId());
    }
}
