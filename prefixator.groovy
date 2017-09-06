#!/usr/bin/env groovy
import java.security.MessageDigest

def md5(String s, limit = 4) {
    MessageDigest digest = MessageDigest.getInstance("MD5")
    digest.update(s.bytes);

    new BigInteger(1, digest.digest()).toString(16).substring(0, limit)
 }

def env = System.getenv()
// nv['GIT_COMMIT'] = "ce9a3c1404e8c91be604088670e93434c4253f03"
//Date date = new Date()
//String prefix = new Date().format("MMdd-HHmmss-") + "${env.BUILD_TAG ?: new Random().nextInt(10 ** 3)}"

def total_before = 0
def total_after = 0
def exceeding = 0
def job_cnt = 0
// 4 chars for '-XXX' hash suffix
// 12 chars for infrared suffixes like '-controller-0'
// 5 chars for allowing us to add 'DDHH-' time prefix
def MAX_SIZE = 63 - 4 - 14 - 5
println("INFO: Max prefix size ${MAX_SIZE} ...")

File fh = new File('jobs.txt')
File output = new File('jobs-after.txt')
File hostnames = new File('hostnames.txt')
hostnames.write ""
output.write ""

fh.each { String j ->
    job_cnt += 1
    // env.BUILD_TAG
    j = j.toLowerCase().replaceAll('_','-')
    .replaceAll(/\./,'')
    .replaceAll('all-in-one','aio')
    .replaceAll('infrared','ir')
    .replaceAll('composable','cp')
    .replaceAll('two-ports','2p')
    .replaceAll('single-port','1p')
    .replaceAll('containers','cnt')
    .replaceAll('ctlplane','cpl')
    .replaceAll('packstack','ps')
    .replaceAll('nightly','n')
    .replaceAll('multijob','mj')
    .replaceAll(/multi-?node/,'mn')
    .replaceAll('virthost','vh')
    .replaceAll('gate','g')
    .replaceAll('risk','r')
    .replaceAll(/(upgrade|update)[s]?/,'u')
    .replaceAll('phase','p')
    .replaceAll('fullstack','fs')
    .replaceAll(/(\d+)-director/,'$1d')
    .replaceAll('network','net')
    .replaceAll('neutron','neu')
    .replaceAll('storage','sto')
    .replaceAll('sahara','sad')
    .replaceAll('manila','man')
    .replaceAll('opendaylight','old')
    .replaceAll('image','i')
    .replaceAll('secgroups','sg')
    .replaceAll('openstack','os')
    .replaceAll('guest','g')
    .replaceAll('custom(ized)?','cst')
    .replaceAll('deploy(ment)?','dpl')
    .replaceAll('stage','st')
    .replaceAll('mixed-versions','mxv')
    .replaceAll('compat','cpt')
    .replaceAll('baremetal','bm')
    .replaceAll('bonding','bnd')
    .replaceAll('performance','perf')
    .replaceAll('workflow','wf')
    .replaceAll('rabbitmq','rmq')
    .replaceAll('monolithic','monolit')
    .replaceAll('-ipv','')
    .replaceAll('-external','-ext')
    .replaceAll('-minimal','-min')
    .replaceAll('-tempest','-tpst')
    .replaceAll('-workarounds','-wkr')

    def l = j.size()
    // |-7\.\d
    // vxlan|vlan
    // -rhel-\d\.\d)
    def j2 = j.replaceAll( /jenkins-|dfg-|-rhos|rhos-|-rhel|python-|-\d(cont|ceph|db|msg|net|comp)|virt|-(from|with)/, '')
    .replaceAll('--','-')
    def l2 = j2.size()

    total_before += l
    total_after += l2

    if (l2 > MAX_SIZE) {
      exceeding += 1
      println("WARN: ${l} -> ${l2} : ${j2}")
    }
    output << "${j2}\n"
    Date date = new Date()
    //String prefix = new Date().format("MMdd-HHmmss-") + "${env.BUILD_TAG ?: new Random().nextInt(10 ** 3)}"

    hostname = date.format('ddHH-') + j2 + '-' + md5(j)
    hostnames << "${hostname}-controller0\n"
}

println("INFO: ${exceeding}/${job_cnt} over the limit")
