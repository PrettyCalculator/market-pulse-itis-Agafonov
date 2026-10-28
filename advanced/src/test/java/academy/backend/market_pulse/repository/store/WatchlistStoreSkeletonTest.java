package academy.backend.market_pulse.repository.store;

import academy.backend.market_pulse.repository.store.impl.WatchlistStoreSkeleton;

/** Проверка моей реализации watchlist-а общим контрактным тестом. */
class WatchlistStoreSkeletonTest extends WatchlistStoreContractTest {

    @Override
    protected WatchlistStore createStore() {
        return new WatchlistStoreSkeleton();
    }
}
