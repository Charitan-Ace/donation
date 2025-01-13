package ace.charitan.donation.internal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class DonationGenerator implements CommandLineRunner {

    @Autowired
    private DonationRepository repository;

    @Override
    public void run(String... args) {

        List<String> projectIds = List.of(
                "233a5ccf-3ed4-4c59-b0e6-3f07072f1a4a", // Middle East Crisis
                "0c112e0e-09a0-4c16-8d87-51e2847055cc", // Ukraine-Russia War
                "cffedd29-972f-41c4-bbe6-54829087caf1", // Food Program in South Africa
                "dda59ffb-0ef5-4a8e-8457-04149f414825", // Yagi Typhoon Support
                "fb51bc08-5ffa-4444-b70a-3410b1bc91ca", // Milton Hurricane Support
                "144a5377-2ccc-4efe-81e8-18a6da36450b", // Helping Ukrainian Refugee
                "9284aa44-9bea-4647-80bd-55c413590404"  // Supporting SOS Children’s Village
        );

        // List of donor IDs
        List<String> donorIds = List.of(
                "a55313de-32be-4b20-867b-b7d07042e629",
                "e99c1730-4f6c-4022-95de-a5487351a938",
                "7819b4f1-8a94-4f8c-99bc-d90be981c719",
                "cd83b706-6e08-4a3b-9433-7f49e19c8a57",
                "2a708f74-3f64-45ab-9e17-92a953f9df5a",
                "b4fc1c09-5082-4a1d-a48c-6870a1c9b6f3",
                "8d3e4329-36d3-4c9e-a7cb-95750573a9bc",
                "ba364d5e-fd36-4f97-9fc3-3d24884365df",
                "f1a6c8b7-c81f-4a9e-b0f1-51e4788133d1",
                "b59f8321-f0f7-4890-880f-70cb925e849e"
        );

        for (int i = 0; i < 100; i++) {
            Donation donation = new Donation();
            donation.setId(null); // Let the database generate the ID
            donation.setAmount(Math.random() * 4900 + 100); // Random amount between 100 and 5000
            donation.setMessage("SKIBIDI TOILET" + (i + 1));
            donation.setProjectId(projectIds.get(i % projectIds.size())); // Cycle through project IDs
            donation.setDonorId(donorIds.get(i % donorIds.size())); // Cycle through donor IDs
            donation.setCreatedAt(null); // Current date

            repository.save(donation);
        }

        System.out.println("Done generating donation");


    }
}
