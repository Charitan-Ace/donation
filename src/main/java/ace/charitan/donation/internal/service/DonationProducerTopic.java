package ace.charitan.donation.internal.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum DonationProducerTopic {
    // PROJECT_GEOGRAPHY_GET_COUNTRY_BY_ISO_CODE("project-geography-get-country-by-iso-code"),
    // PROJECT_MEDIA_GET_MEDIA_BY_PROJECT_ID("project-media-get-media-by-project-id"),
    // //
    // PROJECT_SUBSCRIPTION_GET_MEDIA_BY_PROJECT_ID("project-media-get-media-by-project-id");
    // PROJECT_SUBSCRIPTION_NEW_PROJECT("project-subscription-new-project");

    PROJECT_GET_ALL_PROJECTS_BY_CHARITY_ID("project.get-all-projects-by-charitan-id");

    private final String topic;

}
