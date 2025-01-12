package ace.charitan.donation.internal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class DonationGenerator implements CommandLineRunner {

    @Autowired
    private DonationRepository repository;

    @Override
    public void run(String... args) throws Exception {

        for (int i = 0; i < 100; i++) {
            Donation donation = new Donation(null, Math.random(), "Skibidi toilet", null, "123", "abc", null);

            repository.save(donation);
        }

        System.out.println("Done generating donation");


    }
}
