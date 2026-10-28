package academy.backend.market_pulse.history;

import academy.backend.market_pulse.history.impl.PriceHistoryStoreSkeleton;

/** Проверка моей реализации истории цен общим контрактным тестом. */
class PriceHistoryStoreSkeletonTest extends PriceHistoryStoreContractTest {

    @Override
    protected PriceHistoryStore createStore() {
        return new PriceHistoryStoreSkeleton();
    }
}
