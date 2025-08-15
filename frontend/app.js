document.addEventListener("DOMContentLoaded", () => {
    const menuItemsEl = document.getElementById("menuItems");
    const selectedItemsEl = document.getElementById("selectedItems");
    const placeOrderBtn = document.getElementById("placeOrder");
    const nextOrderBtn = document.getElementById("nextOrder");
    const completeOrderBtn = document.getElementById("completeOrder");
    const queueListEl = document.getElementById("queueList");
    const currentOrderEl = document.getElementById("currentOrder");
    const totalAmountEl = document.getElementById("totalAmount");

    let selectedItems = {};
    let currentOrder = null;
    let menuPrices = {};
// let selectedItems = {};

// Fetch menu on startup
async function loadMenu() {
    const response = await fetch("http://localhost:8080/menu");
    menuPrices = await response.json();
}
loadMenu();

    // Fetch menu and display
    fetch("http://localhost:8080/menu")
        .then(res => res.json())
        .then(menu => {
            for (const [item, price] of Object.entries(menu)) {
                const itemEl = document.createElement("div");
                itemEl.className = "menu-item";
                itemEl.innerHTML = `
                    <span>${item} - $${price.toFixed(2)}</span>
                    <button onclick="addItem('${item}', ${price})">+</button>
                `;
                menuItemsEl.appendChild(itemEl);
            }
        });

    // Add item to order
    window.addItem = (item) => {
    if (!selectedItems[item]) selectedItems[item] = 0;
    selectedItems[item]++;
    updateSelectedItems();
};

    // Update selected items display
    function updateSelectedItems() {
    selectedItemsEl.innerHTML = "";
    let total = 0;

    for (const [item, quantity] of Object.entries(selectedItems)) {
        const price = menuPrices[item] || 0;
        total += price * quantity;

        const itemEl = document.createElement("div");
        itemEl.className = "selected-item";
        itemEl.innerHTML = `
            <span>${item} x${quantity} ($${(price * quantity).toFixed(2)})</span>
            <button onclick="removeItem('${item}')">−</button>
        `;
        selectedItemsEl.appendChild(itemEl);
    }

    totalAmountEl.textContent = total.toFixed(2);
}
    // Remove item from order
    window.removeItem = (item) => {
        if (selectedItems[item]) {
            selectedItems[item]--;
            if (selectedItems[item] === 0) delete selectedItems[item];
            updateSelectedItems();
        }
    };

    // Place order
placeOrderBtn.addEventListener("click", async () => {
    try {
        const response = await fetch("http://localhost:8080/orders", {
            method: "POST",
            mode: "cors",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                customerName: document.getElementById("customerName").value.trim(),
                items: selectedItems,
                isVIP: document.getElementById("isVIP").checked
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || "Failed to place order");
        }

        const data = await response.json();
        alert(`Order placed! ID: ${data.orderId}\nTotal: $${data.totalAmount.toFixed(2)}`);
    } catch (error) {
        console.error("Order error:", error);
        alert(`Error: ${error.message}`);
    }
});
    // Next order (Kitchen)
    nextOrderBtn.addEventListener("click", () => {
        fetch("http://localhost:8080/next")
            .then(res => res.json())
            .then(order => {
                currentOrder = order;
                currentOrderEl.innerHTML = `
                    <h3>${order.customerName} ${order.isVIP ? "🌟 VIP" : ""}</h3>
                    <p>${order.items.map(i => `${i.name} x${i.quantity}`).join(", ")}</p>
                    <p>Total: $${order.totalAmount.toFixed(2)}</p>
                `;
            });
    });

    // Complete order
    completeOrderBtn.addEventListener("click", () => {
        if (!currentOrder) return;
        
        fetch(`http://localhost:8080/complete/${currentOrder.orderId}`)
            .then(() => {
                currentOrderEl.innerHTML = "<p>No order being prepared</p>";
                currentOrder = null;
                refreshQueue();
            });
    });

    // Refresh queue
    function refreshQueue() {
        fetch("http://localhost:8080/queue")
            .then(res => res.json())
            .then(queue => {
                queueListEl.innerHTML = queue.map(order => `
                    <div class="order-card ${order.isVIP ? "vip" : ""}">
                        <h4>${order.customerName} ${order.isVIP ? "🌟 VIP" : ""}</h4>
                        <p>${order.items.map(i => `${i.name} x${i.quantity}`).join(", ")}</p>
                        <p>$${order.totalAmount.toFixed(2)}</p>
                    </div>
                `).join("");
            });
    }

    // Auto-refresh every 5 sec
    setInterval(refreshQueue, 5000);
    refreshQueue();
});