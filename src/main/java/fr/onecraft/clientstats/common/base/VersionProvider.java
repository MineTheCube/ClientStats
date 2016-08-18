package fr.onecraft.clientstats.common.base;

import java.util.UUID;

public interface VersionProvider {

    String getProviderName();

    int getProtocol(UUID player);

}
