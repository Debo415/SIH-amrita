// ---------------- Global State ----------------
let ecoPoints = 150;
let badges = ['Eco Beginner','Product Explorer','Tech Savvy'];
let streak = 7;

// ---------------- Tabs ----------------
function openTab(evt, tabName) {
  const tabcontent = document.getElementsByClassName("tab-content");
  for (let i=0;i<tabcontent.length;i++) tabcontent[i].classList.remove("active");
  const tablinks = document.getElementsByClassName("tab");
  for (let i=0;i<tablinks.length;i++) tablinks[i].classList.remove("active");
  document.getElementById(tabName).classList.add("active");
  evt.currentTarget.classList.add("active");
}

// ---------------- Notifications ----------------
function showNotification(message,type){
  const notification = document.getElementById('notification');
  notification.textContent = message;
  notification.className = notification ${type} show;
  setTimeout(()=>notification.classList.remove('show'),3000);
}

// ---------------- Eco Points ----------------
function updateEcoPointsDisplay(){
  document.getElementById('profilePoints').textContent = 🌟 Eco-Points: ${ecoPoints};
  updateImpact();
}

function awardPoints(points){
  ecoPoints += points;
  updateEcoPointsDisplay();
  showNotification(🎉 You earned ${points} eco-points!,'success');
  checkBadges();
}

function checkBadges(){
  if(ecoPoints>=200 && !badges.includes('Eco Warrior')){
    badges.push('Eco Warrior');
    const span=document.createElement('span');
    span.className='badge';
    span.textContent='🌟 Eco Warrior';
    document.getElementById('badgeContainer').appendChild(span);
    showNotification('🏆 You earned a new badge: Eco Warrior!','success');
  }
}

// ---------------- Rewards ----------------
const rewards = [
  { name: "10% Off Organic Store", cost: 100 },
  { name: "Free Eco Tote Bag", cost: 200 },
  { name: "20% Off Reusable Bottles", cost: 150 },
  { name: "Gift Card ₹500", cost: 400 }
];

function renderRewards(){
  const container = document.getElementById("rewardContainer");
  container.innerHTML = "";
  rewards.forEach(r=>{
    const div = document.createElement("div");
    div.className = "reward-card";
    div.innerHTML = `
      <h4>🎁 ${r.name}</h4>
      <p>Cost: ${r.cost} eco-points</p>
      <button onclick="claimReward('${r.name}', ${r.cost})">Claim</button>
    `;
    container.appendChild(div);
  });
}

function claimReward(name,cost){
  if(ecoPoints>=cost){
    ecoPoints -= cost;
    updateEcoPointsDisplay();
    showNotification(🎁 Claimed: ${name},'success');
  } else {
    const needed = cost - ecoPoints;
    showNotification(❌ Need ${needed} more eco-points,'error');
  }
}

// ---------------- Plans ----------------
const plans = [
  { name: "Eco Starter", price: "₹99/month", features: ["Basic tips", "Monthly eco-report"] },
  { name: "Eco Plus", price: "₹199/month", features: ["Advanced tips", "Exclusive rewards", "Community access"] },
  { name: "Eco Pro", price: "₹299/month", features: ["Personal coaching", "Premium rewards", "Early access to features"] }
];

function renderPlans(){
  const container = document.getElementById("planContainer");
  container.innerHTML = "";
  plans.forEach(p=>{
    const div = document.createElement("div");
    div.className = "plan-card";
    div.innerHTML = `
      <h4>📦 ${p.name}</h4>
      <p><b>${p.price}</b></p>
      <ul>${p.features.map(f=><li>${f}</li>).join('')}</ul>
      <button onclick="selectPlan('${p.name}')">Select</button>
    `;
    container.appendChild(div);
  });
}

function selectPlan(planName){
  showNotification(✅ Plan selected: ${planName},'success');
}

// ---------------- Referral ----------------
function copyReferralCode(){
  const code = document.getElementById('referralCode').textContent;
  navigator.clipboard.writeText(code);
  showNotification('📋 Referral code copied!','success');
}

function shareReferral(){
  const code = document.getElementById('referralCode').textContent;
  if(navigator.share){
    navigator.share({
      title:'Join Sustainable Shopping App',
      text:Use my referral code ${code}
    }).then(()=>showNotification('📤 Referral shared!','success'));
  } else {
    showNotification('❌ Sharing not supported','error');
  }
}

// ---------------- Search ----------------
async function searchProduct(){
  const query=document.getElementById('searchInput').value.trim();
  const resultsContainer=document.getElementById('productResults');
  if(!query){ showNotification('Enter a product name','error'); return; }

  try{
    const url=http://127.0.0.1:8000/food?name=${encodeURIComponent(query)};
    const response=await fetch(url);
    const data=await response.json();
    resultsContainer.innerHTML='';
    if(data.error){
      resultsContainer.innerHTML=<div class="product-card"><h3>🔍 Not found</h3><p>${data.error}</p></div>;
      return;
    }
    data.forEach(product=>displayProduct(product));
    awardPoints(10);
  } catch(err){
    console.error(err);
    showNotification('❌ Failed to fetch','error');
  }
}

function displayProduct(product) {
  const resultsContainer = document.getElementById('productResults');
  let sustainabilityIcon='❌', sustainabilityText='Not Sustainable', sustainabilityColor='red';
  const score=product.sustainability.toLowerCase();
  if(score==='high'){sustainabilityIcon='🌱';sustainabilityText='Sustainable';sustainabilityColor='green';}
  else if(score==='medium'){sustainabilityIcon='⚖';sustainabilityText='Partially Sustainable';sustainabilityColor='orange';}
  const div=document.createElement('div');
  div.className='product-card';
  div.innerHTML=`
    <h3>📊 ${product.name}</h3>
    <div class="metrics">
      <div class="metric">⭐ ${product.taste || product.fabric || '-'}/5<br>Quality</div>
      <div class="metric" style="color:${sustainabilityColor};">${sustainabilityIcon}<br>${sustainabilityText}</div>
    </div>
    <div class="pros-cons">
      <div class="pros">✅ Advantages<ul>${(product.advantages||[]).map(a=><li>${a}</li>).join('')}</ul></div>
      <div class="cons">❌ Disadvantages<ul>${(product.disadvantages||[]).map(d=><li>${d}</li>).join('')}</ul></div>
    </div>
    <div class="recommendations">
      🌱 Recommendations:<br>
      ${(product.recommendations||[]).map(r=>`
        <button onclick="awardPoints(5); showNotification('🌱 Explored ${r}', 'success')">${r}</button>
      `).join('')}
    </div>
  `;
  resultsContainer.appendChild(div);
}

// ---------------- Profile Gamification ----------------
function updateStreak() {
  document.getElementById("streakCount").textContent = streak;
  const progress = Math.min((streak/10)*100,100);
  document.getElementById("streakProgress").style.width=progress+"%";
  if(streak>=5 && !badges.includes("🔥 5-Day Streak")){
    badges.push("🔥 5-Day Streak");
    const span=document.createElement('span'); span.className='badge'; span.textContent="🔥 5-Day Streak";
    document.getElementById('badgeContainer').appendChild(span);
    showNotification("🏆 New Badge: 5-Day Streak!","success");
  }
  if(streak>=10 && !badges.includes("💎 10-Day Streak")){
    badges.push("💎 10-Day Streak");
    const span=document.createElement('span'); span.className='badge'; span.textContent="💎 10-Day Streak";
    document.getElementById('badgeContainer').appendChild(span);
    showNotification("🏆 New Badge: 10-Day Streak!","success");
  }
}

function updateImpact(){
  const trees=Math.floor(ecoPoints/10);
  const water=Math.floor(ecoPoints*0.5);
  const co2=(ecoPoints*0.02).toFixed(1);
  document.getElementById("treesSaved").textContent=trees;
  document.getElementById("waterSaved").textContent=water;
  document.getElementById("co2Reduced").textContent=co2;
}

// ---------------- Init ----------------
updateEcoPointsDisplay();
updateStreak();
renderRewards();
renderPlans();
