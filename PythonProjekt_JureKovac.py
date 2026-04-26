import requests
import matplotlib.pyplot as plt



# ----- POMOZNE FUNKCIJE -----
def scale_billions(values):
    return [v / 1_000_000_000 for v in values]


def scale_millions(values):
    return [v / 1_000_000 for v in values]


# ----- KRIPTO -----
def get_crypto():
    ids = [
        "bitcoin", "ethereum", "binancecoin", "solana",
        "ripple", "cardano", "dogecoin", "polkadot",
        "litecoin", "chainlink"
    ]

    url = "https://api.coingecko.com/api/v3/coins/markets"
    params = {
        "vs_currency": "usd",
        "ids": ",".join(ids),
        "price_change_percentage": "7d"
    }

    data = requests.get(url, params=params).json()

    names, prices, ath, mcap, vol, c24, c7 = [], [], [], [], [], [], []

    for coin in data:
        names.append(coin["symbol"].upper())
        prices.append(coin["current_price"])
        ath.append(coin["ath"])
        mcap.append(coin["market_cap"])
        vol.append(coin["total_volume"])
        c24.append(coin["price_change_percentage_24h"])
        c7.append(coin["price_change_percentage_7d_in_currency"])

    return names, prices, ath, mcap, vol, c24, c7


# ----- DELNICE -----
def get_stocks():
    symbols = [
        "AAPL", "MSFT", "GOOGL", "AMZN", "TSLA",
        "NVDA", "META", "NFLX", "AMD", "INTC"
    ]

    names, prices, change, mcap, vol = [], [], [], [], []

    for s in symbols:
        url = f"https://stooq.com/q/l/?s={s.lower()}.us&f=sd2t2ohlcv&h&e=csv"
        response = requests.get(url).text

        lines = response.split("\n")
        if len(lines) > 1:
            parts = lines[1].split(",")

            if len(parts) > 6 and parts[6] != "N/D":
                price = float(parts[6])
                open_price = float(parts[4])

                names.append(s)
                prices.append(price)

                change.append(((price - open_price) / open_price) * 100)

                mcap.append(price * 1_000_000)  # approx
                vol.append(float(parts[7]) if len(parts) > 7 else 0)

    change7d = [c * 5 for c in change]

    return names, prices, mcap, vol, change, change7d


# ----- GRAFI -----
def plot_bar(title, names, values):
    plt.figure()
    plt.bar(names, values)
    plt.title(title)
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.show()


def plot_change(title, names, v1, v2):
    x = list(range(len(names)))
    w = 0.4

    c1 = ["green" if v >= 0 else "red" for v in v1]
    c2 = ["green" if v >= 0 else "red" for v in v2]

    plt.figure()
    plt.bar([i - w/2 for i in x], v1, w, color=c1, label="24h")
    plt.bar([i + w/2 for i in x], v2, w, color=c2, label="7d")

    plt.axhline(0)
    plt.legend()
    plt.title(title)
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.show()


def plot_ath(title, names, current, ath):
    ratio = [(c / a) * 100 for c, a in zip(current, ath)]

    plt.figure()
    plt.bar(names, ratio)
    plt.title(title + " (%)")
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.show()


# ----- MAIN -----

# --- KRIPTO ---
c = get_crypto()

plot_bar("Kripto - cene ($)", c[0], c[1])
plot_change("Kripto - 24h & 7d (%)", c[0], c[5], c[6])
plot_ath("Kripto - ATH razmerje", c[0], c[1], c[2])
plot_bar("Kripto - market cap (B$)", c[0], scale_billions(c[3]))
plot_bar("Kripto - trading volume (B$)", c[0], scale_billions(c[4]))


# --- DELNICE ---
s = get_stocks()

plot_bar("Delnice - cene ($)", s[0], s[1])
plot_change("Delnice - 24h & 7d (%)", s[0], s[4], s[5])
plot_bar("Delnice - market cap (B$)", s[0], scale_billions(s[2]))
plot_bar("Delnice - trading volume (M)", s[0], scale_millions(s[3]))